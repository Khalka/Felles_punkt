package felleskap.punkt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import felleskap.punkt.Domain.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String firstName;
    private String lastName;
    private String telephone;
    
    @Column(name = "mailaddress", nullable = false, unique = true)
    private String email;
    
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", nullable = false)
    private PostAddress address;

    @JsonIgnore
    @ManyToMany(mappedBy = "registeredUsers")
    private Set<Activity> registeredActivities = new HashSet<>();

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    // Tom konstruktør
    public Users() {
    }

    // Full konstruktør
    public Users(Long userId, String firstName, String lastName, String telephone,
                 String email, String password, Role role, PostAddress address,
                 Set<Activity> registeredActivities) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
        this.registeredActivities = registeredActivities;
    }

    // Getters og setters
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

    public PostAddress getAddress() {
        return address;
    }
    public void setAddress(PostAddress address) {
        this.address = address;
    }

    public Set<Activity> getRegisteredActivities() {
        return registeredActivities;
    }
    public void setRegisteredActivities(Set<Activity> registeredActivities) {
        this.registeredActivities = registeredActivities;
    }

    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
