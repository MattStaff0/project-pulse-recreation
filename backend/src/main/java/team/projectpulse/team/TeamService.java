package team.projectpulse.team;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.InstructorRepository;
import team.projectpulse.student.Student;
import team.projectpulse.student.StudentRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;

    public TeamService(TeamRepository teamRepository, StudentRepository studentRepository, InstructorRepository instructorRepository) {
        this.teamRepository = teamRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
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
        existing.setName(update.getName());
        existing.setDescription(update.getDescription());
        existing.setWebsiteUrl(update.getWebsiteUrl());
        return teamRepository.save(existing);
    }

    public void delete(Long id) {
        Team team = findById(id);
        // Remove students from this team
        for (Student student : team.getStudents()) {
            student.setTeam(null);
            studentRepository.save(student);
        }
        // Remove instructors from this team
        team.getInstructors().clear();
        teamRepository.save(team);
        teamRepository.deleteById(id);
    }

    public void assignStudentToTeam(Long teamId, Long studentId) {
        Team team = findById(teamId);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));
        student.setTeam(team);
        studentRepository.save(student);
    }

    public void removeStudentFromTeam(Long teamId, Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));
        student.setTeam(null);
        studentRepository.save(student);
    }

    public void assignInstructorToTeam(Long teamId, Long instructorId) {
        Team team = findById(teamId);
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", instructorId));
        if (!team.getInstructors().contains(instructor)) {
            team.getInstructors().add(instructor);
            teamRepository.save(team);
        }
    }

    public void removeInstructorFromTeam(Long teamId, Long instructorId) {
        Team team = findById(teamId);
        team.getInstructors().removeIf(i -> i.getId().equals(instructorId));
        teamRepository.save(team);
    }
}
