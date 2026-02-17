package at.atypicalbacklog.management.domain.review;

import jakarta.persistence.*;

@Entity
@Table(name = "yearly_review_category")
public class YearlyReviewCategory {

    @Id
    private String uid;

    @Column(nullable = false)
    private String yearlyReviewUid;

    @Column(nullable = false)
    private String name;

    @Column(name = "\"order\"", nullable = false)
    private int order;

    @Column(nullable = false)
    private boolean valid = true;

    protected YearlyReviewCategory() {}

    public YearlyReviewCategory(
        String uid,
        String yearlyReviewUid,
        String name,
        int order
    ) {
        this.uid = uid;
        this.yearlyReviewUid = yearlyReviewUid;
        this.name = name;
        this.order = order;
        this.valid = true;
    }

    // getters & setters

    public String getUid() { return uid; }
    public String getYearlyReviewUid() { return yearlyReviewUid; }
    public String getName() { return name; }
    public int getOrder() { return order; }
    public boolean isValid() { return valid; }

    public void setName(String name) { this.name = name; }
    public void setOrder(int order) { this.order = order; }
    public void setValid(boolean valid) { this.valid = valid; }
}