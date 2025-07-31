package felleskap.punkt.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Participants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    private LocalDateTime registeredAt;

    public Participants() {
    }

    public Participants(Long id, Users user, Activity activity, LocalDateTime registeredAt) {
        this.id = id;
        this.user = user;
        this.activity = activity;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public Users getUser() {
        return user;
    }

    public Activity getActivity() {
        return activity;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
