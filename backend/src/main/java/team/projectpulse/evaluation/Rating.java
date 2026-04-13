package team.projectpulse.evaluation;

import jakarta.persistence.*;
import team.projectpulse.rubric.Criterion;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "peer_evaluation_id")
    private PeerEvaluation peerEvaluation;

    @ManyToOne
    @JoinColumn(name = "criterion_id")
    private Criterion criterion;

    private Integer score;

    public Rating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PeerEvaluation getPeerEvaluation() { return peerEvaluation; }
    public void setPeerEvaluation(PeerEvaluation peerEvaluation) { this.peerEvaluation = peerEvaluation; }
    public Criterion getCriterion() { return criterion; }
    public void setCriterion(Criterion criterion) { this.criterion = criterion; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}
