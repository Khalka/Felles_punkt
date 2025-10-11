package felleskap.punkt.Repository;

import felleskap.punkt.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    // Spring Data will automatically generate: SELECT u FROM Users u WHERE u.email = ?1
    // The @Column(name = "mailaddress") mapping in the entity will handle the database column
    Optional<Users> findByEmail(String email);
    
    Optional<Users> findByResetToken(String resetToken);
}
