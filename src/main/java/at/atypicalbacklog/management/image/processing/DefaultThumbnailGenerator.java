package at.atypicalbacklog.management.image.processing;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DefaultThumbnailGenerator implements ThumbnailGenerator {

    private static final int MAX_EDGE = 320;
    private static final String OUTPUT_FORMAT = "jpg";

    static {
        ImageIO.scanForPlugins();
    }

    @Override
    public InputStream generateThumbnail(InputStream original) {

        try {
            byte[] data = original.readAllBytes();
            BufferedImage source = ImageIO.read(new ByteArrayInputStream(data));
            if (source == null) {
                throw new IllegalArgumentException("Unsupported or invalid image format");
            }

            Dimension target = calculateTargetSize(
                source.getWidth(),
                source.getHeight()
            );

            BufferedImage thumb = new BufferedImage(
                target.width,
                target.height,
                BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = thumb.createGraphics();
            try {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, target.width, target.height);
                g.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
                );
                g.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
                );
                g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                );

                g.drawImage(
                    source,
                    0,
                    0,
                    target.width,
                    target.height,
                    null
                );
            } finally {
                g.dispose();
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(thumb, OUTPUT_FORMAT, out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new IllegalStateException("Thumbnail generation failed", e);
        }
    }

    private Dimension calculateTargetSize(int width, int height) {
        if (width <= MAX_EDGE && height <= MAX_EDGE) {
            return new Dimension(width, height);
        }

        if (width >= height) {
            int newWidth = MAX_EDGE;
            int newHeight = (int) ((double) height / width * MAX_EDGE);
            return new Dimension(newWidth, newHeight);
        } else {
            int newHeight = MAX_EDGE;
            int newWidth = (int) ((double) width / height * MAX_EDGE);
            return new Dimension(newWidth, newHeight);
        }
    }
}
