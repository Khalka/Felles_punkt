package felleskap.punkt.Repository;

import felleskap.punkt.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantsRepository extends JpaRepository<Participants, Long> {
}
