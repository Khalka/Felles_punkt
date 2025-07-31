package felleskap.punkt.Repository;

import felleskap.punkt.entity.PostAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAddressRepository extends JpaRepository<PostAddress, Long> {
}
