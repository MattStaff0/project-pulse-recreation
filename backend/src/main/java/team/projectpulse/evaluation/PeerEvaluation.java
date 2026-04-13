package team.projectpulse.evaluation;

import jakarta.persistence.*;
import team.projectpulse.student.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PeerEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "evaluator_id")
    private Student evaluator;

    @ManyToOne
    @JoinColumn(name = "evaluatee_id")
    private Student evaluatee;

    private Integer week;

    private Double totalScore;

    @Column(columnDefinition = "TEXT")
    private String publicComment;

    @Column(columnDefinition = "TEXT")
    private String privateComment;

    @OneToMany(mappedBy = "peerEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public PeerEvaluation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getEvaluator() { return evaluator; }
    public void setEvaluator(Student evaluator) { this.evaluator = evaluator; }
    public Student getEvaluatee() { return evaluatee; }
    public void setEvaluatee(Student evaluatee) { this.evaluatee = evaluatee; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }
    public String getPublicComment() { return publicComment; }
    public void setPublicComment(String publicComment) { this.publicComment = publicComment; }
    public String getPrivateComment() { return privateComment; }
    public void setPrivateComment(String privateComment) { this.privateComment = privateComment; }
    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setPeerEvaluation(this);
    }
}
