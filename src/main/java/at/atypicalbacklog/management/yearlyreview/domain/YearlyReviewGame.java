package at.atypicalbacklog.management.yearlyreview.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "yearly_review_game")
public class YearlyReviewGame {

    @Id
    private String uid;

    @Column(nullable = false)
    private String yearlyReviewCategoryUid;

    @Column
    private String gameUid;

    @Column
    private String gameInstanceUid;

    @Column
    private String preferredImageUid;

    @Column(columnDefinition = "text")
    private String text;

    @Column(name = "\"order\"", nullable = false)
    private int order;

    @Column(nullable = false)
    private boolean valid = true;

    protected YearlyReviewGame() {}

    public YearlyReviewGame(
        String uid,
        String yearlyReviewCategoryUid,
        String gameUid,
        String gameInstanceUid,
        String preferredImageUid,
        String text,
        int order
    ) {
        this.uid = uid;
        this.yearlyReviewCategoryUid = yearlyReviewCategoryUid;
        this.gameUid = gameUid;
        this.gameInstanceUid = gameInstanceUid;
        this.preferredImageUid = preferredImageUid;
        this.text = text;
        this.order = order;
        this.valid = true;
    }

    public String getUid() { return uid; }
    public String getYearlyReviewCategoryUid() { return yearlyReviewCategoryUid; }
    public String getGameUid() { return gameUid; }
    public String getGameInstanceUid() { return gameInstanceUid; }
    public String getPreferredImageUid() { return preferredImageUid; }
    public String getText() { return text; }
    public int getOrder() { return order; }
    public boolean isValid() { return valid; }

    public void setGameUid(String gameUid) { this.gameUid = gameUid; }
    public void setGameInstanceUid(String gameInstanceUid) { this.gameInstanceUid = gameInstanceUid; }
    public void setPreferredImageUid(String preferredImageUid) { this.preferredImageUid = preferredImageUid; }
    public void setText(String text) { this.text = text; }
    public void setOrder(int order) { this.order = order; }
    public void setValid(boolean valid) { this.valid = valid; }
}