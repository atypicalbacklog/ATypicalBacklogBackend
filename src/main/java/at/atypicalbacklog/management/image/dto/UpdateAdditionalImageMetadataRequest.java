package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageSource;
import at.atypicalbacklog.management.image.domain.ImageType;

/** All fields optional. Filename and mimetype are not updatable. */
public class UpdateAdditionalImageMetadataRequest {

    private ImageType type;
    private ImageAspectHint aspectHint;
    private ImageSource source;

    public ImageType getType() {
        return type;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public ImageAspectHint getAspectHint() {
        return aspectHint;
    }

    public void setAspectHint(ImageAspectHint aspectHint) {
        this.aspectHint = aspectHint;
    }

    public ImageSource getSource() {
        return source;
    }

    public void setSource(ImageSource source) {
        this.source = source;
    }
}
