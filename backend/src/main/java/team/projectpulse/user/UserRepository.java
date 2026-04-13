package team.projectpulse.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<PeerEvaluationUser, Long> {
    Optional<PeerEvaluationUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
