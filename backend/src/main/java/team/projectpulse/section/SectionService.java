package team.projectpulse.section;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public List<Section> findAll() {
        return sectionRepository.findAll();
    }

    public List<Section> findByCourseId(Long courseId) {
        return sectionRepository.findByCourseId(courseId);
    }

    public Section findById(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("section", id));
    }

    public Section save(Section section) {
        if (sectionRepository.existsByName(section.getName())) {
            throw new IllegalArgumentException("Section name already exists: " + section.getName());
        }
        return sectionRepository.save(section);
    }

    public Section update(Long id, Section update) {
        Section existing = findById(id);
        existing.setName(update.getName());
        existing.setStartDate(update.getStartDate());
        existing.setEndDate(update.getEndDate());
        if (update.getRubric() != null) existing.setRubric(update.getRubric());
        return sectionRepository.save(existing);
    }

    public Section updateActiveWeeks(Long id, String activeWeeks) {
        Section section = findById(id);
        section.setActiveWeeks(activeWeeks);
        return sectionRepository.save(section);
    }

    public void delete(Long id) {
        findById(id);
        sectionRepository.deleteById(id);
    }
}
