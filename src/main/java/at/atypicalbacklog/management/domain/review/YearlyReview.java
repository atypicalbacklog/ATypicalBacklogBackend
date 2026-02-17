package at.atypicalbacklog.management.domain.review;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "yearly_review")
public class YearlyReview {

    @Id
    private String uid;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private boolean valid;

    protected YearlyReview() {}

    public YearlyReview(String uid, int year) {
        this.uid = uid;
        this.year = year;
        this.valid = true;
    }

    public String getUid() { return uid; }
    public int getYear() { return year; }
    public boolean isValid() { return valid; }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}