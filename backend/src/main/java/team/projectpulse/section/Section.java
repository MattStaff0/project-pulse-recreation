package team.projectpulse.section;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import team.projectpulse.course.Course;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.student.Student;
import team.projectpulse.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Section name is required")
    @Column(unique = true)
    private String name;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String activeWeeks; // JSON array of active week numbers

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "rubric_id")
    private Rubric rubric;

    @ManyToMany
    @JoinTable(name = "section_instructor",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id"))
    private List<Instructor> instructors = new ArrayList<>();

    @OneToMany(mappedBy = "section")
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList<>();

    public Section() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getActiveWeeks() { return activeWeeks; }
    public void setActiveWeeks(String activeWeeks) { this.activeWeeks = activeWeeks; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public Rubric getRubric() { return rubric; }
    public void setRubric(Rubric rubric) { this.rubric = rubric; }
    public List<Instructor> getInstructors() { return instructors; }
    public void setInstructors(List<Instructor> instructors) { this.instructors = instructors; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
}
