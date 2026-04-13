package team.projectpulse.rubric;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class RubricService {

    private final RubricRepository rubricRepository;

    public RubricService(RubricRepository rubricRepository) {
        this.rubricRepository = rubricRepository;
    }

    public List<Rubric> findAll() {
        return rubricRepository.findAll();
    }

    public List<Rubric> findByCourseId(Long courseId) {
        return rubricRepository.findByCourseId(courseId);
    }

    public Rubric findById(Long id) {
        return rubricRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("rubric", id));
    }

    public Rubric save(Rubric rubric) {
        if (rubricRepository.existsByName(rubric.getName())) {
            throw new IllegalArgumentException("Rubric name already exists: " + rubric.getName());
        }
        return rubricRepository.save(rubric);
    }

    public Rubric update(Long id, Rubric update) {
        Rubric existing = findById(id);
        existing.setName(update.getName());
        existing.getCriteria().clear();
        for (Criterion c : update.getCriteria()) {
            existing.addCriterion(c);
        }
        return rubricRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        rubricRepository.deleteById(id);
    }
}
