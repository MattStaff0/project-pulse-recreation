package team.projectpulse.student;

import jakarta.persistence.*;
import team.projectpulse.section.Section;
import team.projectpulse.team.Team;
import team.projectpulse.user.PeerEvaluationUser;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends PeerEvaluationUser {

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Student() {
        setRoles("student");
    }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
}
