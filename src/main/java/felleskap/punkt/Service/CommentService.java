package felleskap.punkt.Service;

import felleskap.punkt.Repository.CommentRepository;
import felleskap.punkt.Repository.ActivityRepository;
import felleskap.punkt.Repository.UsersRepository;
import felleskap.punkt.entity.Comment;
import felleskap.punkt.entity.Activity;
import felleskap.punkt.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UsersRepository userRepository;

    public List<Comment> getCommentsByActivityId(Long activityId) {
        return commentRepository.findByActivity_ActivityIdOrderByCreatedAtDesc(activityId);
    }

    public Comment addComment(Long activityId, String text, String userEmail) {
        Optional<Activity> activity = activityRepository.findById(activityId);
        Optional<Users> user = userRepository.findByEmail(userEmail);

        if (activity.isPresent() && user.isPresent()) {
            Comment comment = new Comment(text, activity.get(), user.get());
            return commentRepository.save(comment);
        }
        throw new RuntimeException("Activity or User not found");
    }

    public void deleteComment(Long commentId, String userEmail) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent() && comment.get().getUser().getEmail().equals(userEmail)) {
            commentRepository.deleteById(commentId);
        } else {
            throw new RuntimeException("Comment not found or unauthorized");
        }
    }
}
