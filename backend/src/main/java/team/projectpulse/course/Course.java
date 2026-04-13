package team.projectpulse.course;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.section.Section;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Course name is required")
    @Column(unique = true)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Instructor courseAdmin;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Rubric> rubrics = new ArrayList<>();

    public Course() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instructor getCourseAdmin() { return courseAdmin; }
    public void setCourseAdmin(Instructor courseAdmin) { this.courseAdmin = courseAdmin; }
    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Rubric> getRubrics() { return rubrics; }
    public void setRubrics(List<Rubric> rubrics) { this.rubrics = rubrics; }
}
