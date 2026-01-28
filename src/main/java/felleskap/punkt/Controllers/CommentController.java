package felleskap.punkt.Controllers;

import felleskap.punkt.Service.CommentService;
import felleskap.punkt.entity.Comment;
import felleskap.punkt.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {

    @Autowired
    private CommentService commentService;
    
    @Autowired
    private JwtService jwtService;

    @GetMapping("/activity/{activityId}")
    public List<Comment> getCommentsByActivity(@PathVariable Long activityId) {
        return commentService.getCommentsByActivityId(activityId);
    }

    @PostMapping("/activity/{activityId}")
    public Comment addComment(@PathVariable Long activityId, @RequestBody Map<String, String> request, @RequestHeader("Authorization") String authHeader) {
        String userEmail = extractEmailFromToken(authHeader);
        String text = request.get("text");
        return commentService.addComment(activityId, text, userEmail);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId, @RequestHeader("Authorization") String authHeader) {
        String userEmail = extractEmailFromToken(authHeader);
        commentService.deleteComment(commentId, userEmail);
    }

    private String extractEmailFromToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.extractUsername(token);
        }
        return null;
    }
}
