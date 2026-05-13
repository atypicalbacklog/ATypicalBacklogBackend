package at.atypicalbacklog.management.image.dto;

import at.atypicalbacklog.management.image.domain.ImageAspectHint;
import at.atypicalbacklog.management.image.domain.ImageOwnerType;
import at.atypicalbacklog.management.image.domain.ImageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

/** All fields optional. Source, filename, mimetype are not updatable. */
public class UpdateImageMetadataRequest {

    private ImageOwnerType ownerType;
    private String gameUid;
    private String gameInstanceUid;
    private String dlcUid;
    private ImageType type;
    private ImageAspectHint aspectHint;
    private Boolean primaryImage;

    @JsonIgnore
    private boolean gameUidPresent;
    @JsonIgnore
    private boolean gameInstanceUidPresent;
    @JsonIgnore
    private boolean dlcUidPresent;

    public ImageOwnerType getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(ImageOwnerType ownerType) {
        this.ownerType = ownerType;
    }

    public String getGameUid() {
        return gameUid;
    }

    @JsonSetter("gameUid")
    public void setGameUid(String gameUid) {
        this.gameUid = gameUid;
        this.gameUidPresent = true;
    }

    public String getGameInstanceUid() {
        return gameInstanceUid;
    }

    @JsonSetter("gameInstanceUid")
    public void setGameInstanceUid(String gameInstanceUid) {
        this.gameInstanceUid = gameInstanceUid;
        this.gameInstanceUidPresent = true;
    }

    public String getDlcUid() {
        return dlcUid;
    }

    @JsonSetter("dlcUid")
    public void setDlcUid(String dlcUid) {
        this.dlcUid = dlcUid;
        this.dlcUidPresent = true;
    }

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

    public Boolean getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(Boolean primaryImage) {
        this.primaryImage = primaryImage;
    }

    public boolean hasGameUid() {
        return gameUidPresent;
    }

    public boolean hasGameInstanceUid() {
        return gameInstanceUidPresent;
    }

    public boolean hasDlcUid() {
        return dlcUidPresent;
    }
}
