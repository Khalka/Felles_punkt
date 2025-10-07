package felleskap.punkt.dto;

import felleskap.punkt.Domain.Role;
import felleskap.punkt.entity.PostAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String telephone;
    private String email;
    private String password;
    private Role role;
    private PostAddress address;

    // Tom konstruktør
    public RegisterRequest() {
    }

    // Full konstruktør
    public RegisterRequest(String firstName, String lastName, String telephone,
                           String email, String password, Role role, PostAddress address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
        this.role = role;
        this.address = address;
    }

    // Gettere og settere
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
}