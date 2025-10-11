package felleskap.punkt.Repository;

import felleskap.punkt.entity.Activity;
import felleskap.punkt.entity.Organizer;
import felleskap.punkt.entity.Users;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Hent aktiviteter for en gitt arrangør basert på arrangørens e-post
    List<Activity> findByOrganizerEmail(String mail);

    @Query("SELECT a FROM Activity a JOIN a.registeredUsers u WHERE u = :user " +
           "AND ((a.startTime < :endTime AND a.endTime > :startTime))")
    List<Activity> findConflictingActivitiesForUser(
        @Param("user") Users user,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
