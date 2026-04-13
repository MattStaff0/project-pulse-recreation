package team.projectpulse.instructor;

import jakarta.persistence.*;
import team.projectpulse.course.Course;
import team.projectpulse.section.Section;
import team.projectpulse.team.Team;
import team.projectpulse.user.PeerEvaluationUser;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("INSTRUCTOR")
public class Instructor extends PeerEvaluationUser {

    @ManyToMany(mappedBy = "instructors")
    private List<Section> sections = new ArrayList<>();

    @ManyToMany(mappedBy = "instructors")
    private List<Team> teams = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "default_course_id")
    private Course defaultCourse;

    @ManyToOne
    @JoinColumn(name = "default_section_id")
    private Section defaultSection;

    public Instructor() {
        setRoles("instructor");
    }

    public List<Section> getSections() { return sections; }
    public void setSections(List<Section> sections) { this.sections = sections; }
    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
    public Course getDefaultCourse() { return defaultCourse; }
    public void setDefaultCourse(Course defaultCourse) { this.defaultCourse = defaultCourse; }
    public Section getDefaultSection() { return defaultSection; }
    public void setDefaultSection(Section defaultSection) { this.defaultSection = defaultSection; }
}
