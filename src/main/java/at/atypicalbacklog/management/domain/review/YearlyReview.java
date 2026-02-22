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

    @Column(name = "intro_text")
    private String introText;
    
    @Column(name = "outro_text")
    private String outroText;

    protected YearlyReview() {}

    public YearlyReview(String uid, int year) {
        this.uid = uid;
        this.year = year;
        this.valid = true;
    }

    public String getUid() { return uid; }
    public int getYear() { return year; }
    public boolean isValid() { return valid; }
    public String getIntroText() { return introText; }
    public String getOutroText() { return outroText; }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    public void setIntroText(String introText) { this.introText = introText; }
    public void setOutroText(String outroText) { this.outroText = outroText; }
}