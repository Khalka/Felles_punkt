package felleskap.punkt.Repository;

import felleskap.punkt.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationsRepository extends JpaRepository<Locations, Long> {
}
