package at.atypicalbacklog.management.moderation;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Local ONNX toxicity scorer. Expects model directory with model.onnx (or model.quant.onnx) and tokenizer.json.
 * Fail-open when disabled or model missing.
 */
@Service
public class ToxicityService {

    private static final Logger log = LoggerFactory.getLogger(ToxicityService.class);
    private static final int MAX_LENGTH = 128;

    private final boolean enabled;
    private final Path modelDir;
    private final double threshold;

    private OrtEnvironment env;
    private OrtSession session;
    private HuggingFaceTokenizer tokenizer;
    private String logitsOutputName;
    private volatile boolean ready;

    public ToxicityService(
        @Value("${app.moderation.toxicity.enabled:false}") boolean enabled,
        @Value("${app.moderation.model.path:models/toxic-bert}") String modelPath,
        @Value("${app.moderation.toxicity.threshold:0.75}") double threshold
    ) {
        this.enabled = enabled;
        this.modelDir = Path.of(modelPath);
        this.threshold = threshold;
    }

    @PostConstruct
    void init() {
        if (!enabled) {
            log.info("Toxicity model disabled");
            return;
        }
        try {
            Path onnx = resolveOnnxFile(modelDir);
            Path tokenizerPath = modelDir.resolve("tokenizer.json");
            if (!Files.isRegularFile(tokenizerPath)) {
                throw new IllegalStateException("tokenizer.json not found in " + modelDir.toAbsolutePath());
            }

            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions options = new OrtSession.SessionOptions();
            options.setIntraOpNumThreads(2);
            options.setInterOpNumThreads(2);
            session = env.createSession(onnx.toString(), options);
            tokenizer = HuggingFaceTokenizer.builder()
                .optTokenizerPath(tokenizerPath)
                .optTruncation(true)
                .optMaxLength(MAX_LENGTH)
                .optPadding(true)
                .build();

            Optional<String> logits = session.getOutputNames().stream()
                .filter(n -> n.toLowerCase(Locale.ROOT).contains("logit") || "output".equals(n))
                .findFirst();
            logitsOutputName = logits.orElseGet(() -> session.getOutputNames().iterator().next());

            ready = true;
            log.info("Toxicity ONNX model loaded from {} (output={})", onnx.toAbsolutePath(), logitsOutputName);
        } catch (Exception e) {
            ready = false;
            log.warn(
                "Toxicity model not available (fail-open). path={} error={}",
                modelDir.toAbsolutePath(),
                e.getMessage()
            );
            closeQuietly();
        }
    }

    @PreDestroy
    void destroy() {
        closeQuietly();
    }

    public boolean isReady() {
        return ready;
    }

    public double threshold() {
        return threshold;
    }

    public ToxicityResult analyze(String text) {
        if (!enabled || !ready || text == null || text.isBlank()) {
            return ToxicityResult.unavailable();
        }
        try {
            Encoding encoding = tokenizer.encode(text);
            long[] ids = encoding.getIds();
            long[] mask = encoding.getAttentionMask();
            if (ids.length == 0) {
                return new ToxicityResult(0, List.of(), 0, true);
            }

            Map<String, OnnxTensor> inputs = new HashMap<>();
            try {
                for (String inputName : session.getInputNames()) {
                    String lower = inputName.toLowerCase(Locale.ROOT);
                    if (lower.contains("token_type")) {
                        inputs.put(inputName, OnnxTensor.createTensor(env, toBatch(new long[ids.length])));
                    } else if (lower.contains("mask")) {
                        inputs.put(inputName, OnnxTensor.createTensor(env, toBatch(mask)));
                    } else {
                        inputs.put(inputName, OnnxTensor.createTensor(env, toBatch(ids)));
                    }
                }

                try (OrtSession.Result result = session.run(inputs)) {
                    float[][] logits = (float[][]) result.get(logitsOutputName).get().getValue();
                    float[] row = logits[0];
                    double[] probs = sigmoid(row);
                    int bestIdx = 0;
                    double best = probs[0];
                    List<String> labels = new ArrayList<>();
                    for (int i = 0; i < probs.length; i++) {
                        if (probs[i] >= threshold) {
                            labels.add("label_" + i);
                        }
                        if (probs[i] > best) {
                            best = probs[i];
                            bestIdx = i;
                        }
                    }
                    if (labels.isEmpty() && best >= threshold) {
                        labels.add("label_" + bestIdx);
                    }
                    // Prefer max score as toxicity score (multi-label detoxify-style models)
                    return new ToxicityResult(best, List.copyOf(labels), best, true);
                }
            } finally {
                for (OnnxTensor t : inputs.values()) {
                    t.close();
                }
            }
        } catch (Exception e) {
            log.warn("Toxicity inference failed (fail-open): {}", e.getMessage());
            return ToxicityResult.unavailable();
        }
    }

    private static Path resolveOnnxFile(Path dir) throws OrtException {
        Path quant = dir.resolve("model.quant.onnx");
        Path plain = dir.resolve("model.onnx");
        if (Files.isRegularFile(quant)) {
            return quant;
        }
        if (Files.isRegularFile(plain)) {
            return plain;
        }
        throw new OrtException("No model.onnx or model.quant.onnx in " + dir.toAbsolutePath());
    }

    private static long[][] toBatch(long[] values) {
        return new long[][] { values };
    }

    private static double[] sigmoid(float[] logits) {
        double[] out = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            out[i] = 1.0 / (1.0 + Math.exp(-logits[i]));
        }
        return out;
    }

    private void closeQuietly() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception ignored) {
        }
        session = null;
        tokenizer = null;
        ready = false;
    }
}
