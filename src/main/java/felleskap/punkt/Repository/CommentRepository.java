package felleskap.punkt.Repository;

import felleskap.punkt.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByActivity_ActivityIdOrderByCreatedAtDesc(Long activityId);
}
