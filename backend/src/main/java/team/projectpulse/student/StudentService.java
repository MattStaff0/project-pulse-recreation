package team.projectpulse.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findBySectionId(Long sectionId) {
        return studentRepository.findBySectionId(sectionId);
    }

    public List<Student> findByTeamId(Long teamId) {
        return studentRepository.findByTeamId(teamId);
    }

    public List<Student> findUnassignedBySectionId(Long sectionId) {
        return studentRepository.findBySectionIdAndTeamIsNull(sectionId);
    }

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("student", id));
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public Student update(Long id, Student update) {
        Student existing = findById(id);
        existing.setFirstName(update.getFirstName());
        existing.setLastName(update.getLastName());
        existing.setEmail(update.getEmail());
        return studentRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        studentRepository.deleteById(id);
    }
}
