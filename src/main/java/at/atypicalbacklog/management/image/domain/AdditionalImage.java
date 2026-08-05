package at.atypicalbacklog.management.image.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "additional_image")
public class AdditionalImage {

    @Id
    private String uid;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String mimetype;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageSource source;

    @Enumerated(EnumType.STRING)
    @Column(name = "aspect_hint", nullable = false)
    private ImageAspectHint aspectHint;

    protected AdditionalImage() {}

    public AdditionalImage(
        String uid,
        String filename,
        String mimetype,
        ImageType type,
        ImageSource source,
        ImageAspectHint aspectHint
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.filename = filename;
        this.mimetype = mimetype;
        this.type = type;
        this.source = source;
        this.aspectHint = aspectHint;
    }

    public void setType(ImageType type) {
        this.type = type;
    }

    public void setAspectHint(ImageAspectHint aspectHint) {
        this.aspectHint = aspectHint;
    }

    public void setSource(ImageSource source) {
        this.source = source;
    }

    public String getUid() { return uid; }
    public Instant getCreationDate() { return creationDate; }
    public String getFilename() { return filename; }
    public String getMimetype() { return mimetype; }
    public ImageType getType() { return type; }
    public ImageSource getSource() { return source; }
    public ImageAspectHint getAspectHint() { return aspectHint; }
}
