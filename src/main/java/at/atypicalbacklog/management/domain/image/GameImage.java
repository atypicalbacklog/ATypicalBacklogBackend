package at.atypicalbacklog.management.domain.image;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "game_image")
public class GameImage {

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
    private ImageOwnerType ownerType;

    @Column(name = "game_uid")
    private String gameUid;

    @Column(name = "game_instance_uid")
    private String gameInstanceUid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageAspectHint aspectHint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageSource source;

    @Column(nullable = false)
    private boolean primaryImage;

    protected GameImage() {}

    public GameImage(
        String uid,
        String filename,
        String mimetype,
        ImageOwnerType ownerType,
        String gameUid,
        String gameInstanceUid,
        ImageType type,
        ImageAspectHint aspectHint,
        ImageSource source,
        boolean primaryImage
    ) {
        this.uid = uid;
        this.creationDate = Instant.now();
        this.filename = filename;
        this.mimetype = mimetype;
        this.ownerType = ownerType;
        this.gameUid = gameUid;
        this.gameInstanceUid = gameInstanceUid;
        this.type = type;
        this.aspectHint = aspectHint;
        this.source = source;
        this.primaryImage = primaryImage;
    }

    public void setPrimaryImage(boolean primaryImage) {
        this.primaryImage = primaryImage;
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

    // getters only
    public String getUid() {
        return uid;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public ImageOwnerType getOwnerType() {
        return ownerType;
    }
    
    public String getGameUid() {
        return gameUid;
    }

    public String getGameInstanceUid() {
        return gameInstanceUid;
    }
    
    public ImageType getType() {
        return type;
    }

    public ImageAspectHint getAspectHint() {
        return aspectHint;
    }

    public ImageSource getSource() {
        return source;
    }

    public boolean isPrimaryImage() {
        return primaryImage;
    }
}
