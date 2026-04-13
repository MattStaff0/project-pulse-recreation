package team.projectpulse.activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import team.projectpulse.student.Student;
import team.projectpulse.team.Team;

import java.time.LocalDateTime;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Category is required")
    private String category; // DEVELOPMENT, TESTING, BUGFIX, etc.

    @NotEmpty(message = "Activity title is required")
    @Column(name = "activity_title")
    private String activity;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double plannedHours;
    private Double actualHours;

    private String status; // IN_PROGRESS, UNDER_TESTING, DONE

    @Column(columnDefinition = "TEXT")
    private String comments;

    private Integer week;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public Activity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getPlannedHours() { return plannedHours; }
    public void setPlannedHours(Double plannedHours) { this.plannedHours = plannedHours; }
    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double actualHours) { this.actualHours = actualHours; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
