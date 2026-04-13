package team.projectpulse.rubric;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import team.projectpulse.course.Course;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Rubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Rubric name is required")
    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "sort_order")
    private List<Criterion> criteria = new ArrayList<>();

    public Rubric() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public List<Criterion> getCriteria() { return criteria; }
    public void setCriteria(List<Criterion> criteria) { this.criteria = criteria; }

    public void addCriterion(Criterion criterion) {
        criteria.add(criterion);
        criterion.setRubric(this);
    }
}
