package felleskap.punkt.Repository;

import felleskap.punkt.entity.Activity;
import felleskap.punkt.entity.Organizer;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Hent aktiviteter for en gitt arrangør basert på arrangørens e-post
    List<Activity> findByOrganizerEmail(String mail);

}
