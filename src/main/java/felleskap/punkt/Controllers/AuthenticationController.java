package felleskap.punkt.Controllers;

import felleskap.punkt.Domain.Role;
import felleskap.punkt.Repository.UsersRepository;
import felleskap.punkt.entity.Users;
import felleskap.punkt.security.jwt.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            UsersRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // DTO for login request
    public static class LoginRequest {
        @NotBlank(message = "Brukernavn kan ikke være tomt")
        private String username;

        @NotBlank(message = "Passord kan ikke være tomt")
        private String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    // DTO for login response
    public static class LoginResponse {
        private String token;
        private String role;

        public LoginResponse(String token, String role) {
            this.token = token;
            this.role = role;
        }

        public String getToken() {
            return token;
        }
        public String getRole() {
            return role;
        }
    }

    // DTO for register request (utvid med flere felt ved behov)
    public static class RegisterRequest {
        @NotBlank(message = "E-post kan ikke være tom")
        private String email;

        @NotBlank(message = "Passord kan ikke være tomt")
        private String password;

        @NotBlank(message = "Rolle må oppgis")
        private String role;

        // Eksempel på ekstra felter hvis ønskelig
        private String firstName;
        private String lastName;
        private Long telephone;

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
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public Long getTelephone() { return telephone; }
        public void setTelephone(Long telephone) { this.telephone = telephone; }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }

        Optional<Users> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-post er allerede registrert"));
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ugyldig rolle"));
        }

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTelephone(request.getTelephone());
        // Husk at hvis Users krever adresse, må du håndtere det her også (kan være null for nå)

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Registrering vellykket"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }

        try {
            var authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
            authenticationManager.authenticate(authToken);

            var userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String jwt = jwtService.generateToken(userDetails);

            Users user = userRepository.findByEmail(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Bruker ikke funnet"));

            return ResponseEntity.ok(new LoginResponse(jwt, user.getRole().name()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body(Map.of("message", "Feil brukernavn eller passord."));
        }
    }
}
