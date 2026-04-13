package team.projectpulse.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<UserInvitation, Long> {
    Optional<UserInvitation> findByToken(String token);
    Optional<UserInvitation> findByEmailAndAcceptedFalse(String email);
}
