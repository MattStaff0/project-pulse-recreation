package team.projectpulse.team;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.activity.ActivityRepository;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;
import team.projectpulse.evaluation.PeerEvaluationRepository;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final ActivityRepository activityRepository;
    private final PeerEvaluationRepository peerEvaluationRepository;

    public TeamService(TeamRepository teamRepository,
                       StudentRepository studentRepository,
                       InstructorRepository instructorRepository,
                       ActivityRepository activityRepository,
                       PeerEvaluationRepository peerEvaluationRepository) {
        this.teamRepository = teamRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.activityRepository = activityRepository;
        this.peerEvaluationRepository = peerEvaluationRepository;
    }

    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    public List<Team> findBySectionId(Long sectionId) {
        return teamRepository.findBySectionId(sectionId);
    }

    public Team findById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("team", id));
    }

    public Team save(Team team) {
        if (teamRepository.existsByName(team.getName())) {
            throw new IllegalArgumentException("Team name already exists: " + team.getName());
        }
        return teamRepository.save(team);
    }

    public Team update(Long id, Team update) {
        Team existing = findById(id);
        if (teamRepository.existsByNameAndIdNot(update.getName(), id)) {
            throw new IllegalArgumentException("Team name already exists: " + update.getName());
        }
        existing.setName(update.getName());
        existing.setDescription(update.getDescription());
        existing.setWebsiteUrl(update.getWebsiteUrl());
        return teamRepository.save(existing);
    }

    public void delete(Long id) {
        Team team = findById(id);
        List<Student> teamStudents = new ArrayList<>(team.getStudents());
        // Remove students from this team
        for (Student student : teamStudents) {
            activityRepository.deleteByStudentId(student.getId());
            peerEvaluationRepository.deleteByEvaluatorIdOrEvaluateeId(student.getId(), student.getId());
            student.setTeam(null);
            studentRepository.save(student);
        }
        activityRepository.deleteByTeamId(id);
        // Remove instructors from this team
        team.getInstructors().clear();
        teamRepository.save(team);
        teamRepository.deleteById(id);
    }

    public void assignStudentToTeam(Long teamId, Long studentId) {
        Team team = findById(teamId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));
        if (team.getSection() == null || student.getSection() == null
                || !team.getSection().getId().equals(student.getSection().getId())) {
            throw new IllegalArgumentException("Student and team must belong to the same section");
        }
        student.setTeam(team);
        studentRepository.save(student);
    }

    public void removeStudentFromTeam(Long teamId, Long studentId) {
        Team team = findById(teamId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));
        if (student.getTeam() == null || !student.getTeam().getId().equals(team.getId())) {
            throw new IllegalArgumentException("Student is not assigned to this team");
        }
        student.setTeam(null);
        studentRepository.save(student);
    }

    public void assignInstructorToTeam(Long teamId, Long instructorId) {
        Team team = findById(teamId);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", instructorId));
        if (team.getSection() == null) {
            throw new IllegalArgumentException("Team must belong to a section before assigning instructors");
        }
        boolean belongsToSection = instructor.getSections().stream()
                .anyMatch(section -> section.getId().equals(team.getSection().getId()));
        if (!belongsToSection) {
            team.getSection().getInstructors().add(instructor);
        }
        if (!team.getInstructors().contains(instructor)) {
            team.getInstructors().add(instructor);
            teamRepository.save(team);
        }
    }

    public void removeInstructorFromTeam(Long teamId, Long instructorId) {
        Team team = findById(teamId);
        boolean assignedToTeam = team.getInstructors().stream().anyMatch(i -> i.getId().equals(instructorId));
        if (!assignedToTeam) {
            throw new IllegalArgumentException("Instructor is not assigned to this team");
        }
        if (team.getInstructors().size() <= 1) {
            throw new IllegalArgumentException("Every team must have at least one instructor");
        }
        team.getInstructors().removeIf(i -> i.getId().equals(instructorId));
        teamRepository.save(team);
    }
}
