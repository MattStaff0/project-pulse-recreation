package team.projectpulse.instructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("instructor", id));
    }

    public Instructor save(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    public Instructor update(Long id, Instructor update) {
        Instructor existing = findById(id);
        existing.setFirstName(update.getFirstName());
        existing.setLastName(update.getLastName());
        existing.setEmail(update.getEmail());
        return instructorRepository.save(existing);
    }

    public void deactivate(Long id) {
        Instructor instructor = findById(id);
        instructor.setEnabled(false);
        instructorRepository.save(instructor);
    }

    public void reactivate(Long id) {
        Instructor instructor = findById(id);
        instructor.setEnabled(true);
        instructorRepository.save(instructor);
    }
}
