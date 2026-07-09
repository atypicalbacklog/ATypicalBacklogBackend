package at.atypicalbacklog.management.backlogrecommendation.service;

import at.atypicalbacklog.management.audit.AuditRecorder;
import at.atypicalbacklog.management.config.validation.PublicInputValidation;
import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendation;
import at.atypicalbacklog.management.backlogrecommendation.domain.BacklogRecommendationComment;
import at.atypicalbacklog.management.backlogrecommendation.domain.GameRecommendation;
import at.atypicalbacklog.management.backlogrecommendation.dto.AddBacklogRecommendationCommentRequest;
import at.atypicalbacklog.management.backlogrecommendation.dto.BacklogRecommendationInput;
import at.atypicalbacklog.management.backlogrecommendation.dto.CreateBacklogRecommendationRequest;
import at.atypicalbacklog.management.backlogrecommendation.dto.CreateBacklogRecommendationResponse;
import at.atypicalbacklog.management.backlogrecommendation.dto.GameRecommendationInput;
import at.atypicalbacklog.management.backlogrecommendation.repository.BacklogRecommendationCommentRepository;
import at.atypicalbacklog.management.backlogrecommendation.repository.BacklogRecommendationRepository;
import at.atypicalbacklog.management.backlogrecommendation.repository.GameRecommendationRepository;
import at.atypicalbacklog.management.game.repository.GameRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class BacklogRecommendationCommandService {

    private final GameRecommendationRepository gameRecommendationRepo;
    private final BacklogRecommendationRepository backlogRecommendationRepo;
    private final BacklogRecommendationCommentRepository commentRepo;
    private final GameRepository gameRepo;
    private final AuditRecorder audit;

    public BacklogRecommendationCommandService(
        GameRecommendationRepository gameRecommendationRepo,
        BacklogRecommendationRepository backlogRecommendationRepo,
        BacklogRecommendationCommentRepository commentRepo,
        GameRepository gameRepo,
        AuditRecorder audit
    ) {
        this.gameRecommendationRepo = gameRecommendationRepo;
        this.backlogRecommendationRepo = backlogRecommendationRepo;
        this.commentRepo = commentRepo;
        this.gameRepo = gameRepo;
        this.audit = audit;
    }

    @Transactional
    public CreateBacklogRecommendationResponse create(CreateBacklogRecommendationRequest req) {
        GameRecommendationInput gameInput = req.gameRecommendation();
        BacklogRecommendationInput backlogInput = req.backlogRecommendation();

        if (gameInput == null || backlogInput == null) {
            throw new IllegalArgumentException("gameRecommendation and backlogRecommendation are required");
        }

        String gameRecommendationUid = resolveOrCreateGameRecommendation(gameInput);

        if (backlogInput.gameUid() != null && !gameRepo.existsById(backlogInput.gameUid())) {
            throw new IllegalArgumentException("Game not found: " + backlogInput.gameUid());
        }

        PublicInputValidation.requireRecommendationName(backlogInput.name());

        BacklogRecommendation recommendation = new BacklogRecommendation(
            UUID.randomUUID().toString(),
            gameRecommendationUid,
            backlogInput.gameUid(),
            backlogInput.name(),
            backlogInput.originalRecommendationBy()
        );

        backlogRecommendationRepo.save(recommendation);

        audit.record(
            "Create Backlog Recommendation",
            "BacklogRecommendation",
            "CREATE",
            Map.of("uid", recommendation.getUid()),
            null,
            recommendation,
            null
        );

        if (backlogInput.comment() != null && !backlogInput.comment().isBlank()) {
            PublicInputValidation.requireComment(backlogInput.comment());
            BacklogRecommendationComment comment = new BacklogRecommendationComment(
                UUID.randomUUID().toString(),
                recommendation.getUid(),
                backlogInput.comment()
            );
            commentRepo.save(comment);

            audit.record(
                "Add Backlog Recommendation Comment",
                "BacklogRecommendationComment",
                "CREATE",
                Map.of("backlogRecommendationUid", recommendation.getUid()),
                null,
                comment,
                null
            );
        }

        return new CreateBacklogRecommendationResponse(
            recommendation.getUid(),
            gameRecommendationUid
        );
    }

    @Transactional
    public void upvote(String uid) {
        BacklogRecommendation recommendation = backlogRecommendationRepo.findById(uid)
            .orElseThrow(() -> new IllegalArgumentException("BacklogRecommendation not found"));

        BacklogRecommendation before = recommendation;
        recommendation.upvote();
        backlogRecommendationRepo.save(recommendation);

        audit.record(
            "Upvote Backlog Recommendation",
            "BacklogRecommendation",
            "UPDATE",
            Map.of("uid", uid),
            before,
            recommendation,
            null
        );
    }

    @Transactional
    public void addComment(String backlogRecommendationUid, AddBacklogRecommendationCommentRequest req) {
        if (req == null || req.comment() == null || req.comment().isBlank()) {
            throw new IllegalArgumentException("comment is required");
        }
        PublicInputValidation.requireComment(req.comment());

        if (!backlogRecommendationRepo.existsById(backlogRecommendationUid)) {
            throw new IllegalArgumentException("BacklogRecommendation not found");
        }

        BacklogRecommendationComment comment = new BacklogRecommendationComment(
            UUID.randomUUID().toString(),
            backlogRecommendationUid,
            req.comment()
        );

        commentRepo.save(comment);

        audit.record(
            "Add Backlog Recommendation Comment",
            "BacklogRecommendationComment",
            "CREATE",
            Map.of("backlogRecommendationUid", backlogRecommendationUid),
            null,
            comment,
            null
        );
    }

    private String resolveOrCreateGameRecommendation(GameRecommendationInput input) {
        if (input.uid() != null && !input.uid().isBlank()) {
            gameRecommendationRepo.findById(input.uid())
                .orElseThrow(() -> new IllegalArgumentException("GameRecommendation not found: " + input.uid()));
            return input.uid();
        }

        if (input.nameBase() == null || input.nameBase().isBlank()) {
            throw new IllegalArgumentException("gameRecommendation.nameBase is required when uid is not provided");
        }

        GameRecommendation created = new GameRecommendation(
            UUID.randomUUID().toString(),
            input.nameBase(),
            input.releaseDate(),
            input.externalIgdbId()
        );
        gameRecommendationRepo.save(created);
        return created.getUid();
    }
}
