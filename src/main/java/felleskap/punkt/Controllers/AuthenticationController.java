package felleskap.punkt.Controllers;

import felleskap.punkt.Domain.Role;
import felleskap.punkt.Repository.UsersRepository;
import felleskap.punkt.Repository.OrganizerRepository;
import felleskap.punkt.dto.RegisterRequest;
import felleskap.punkt.entity.Users;
import felleskap.punkt.entity.Organizer;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizerRepository organizerRepository;

    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            UsersRepository userRepository,
            PasswordEncoder passwordEncoder,
            OrganizerRepository organizerRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizerRepository = organizerRepository;
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

    // DTOs for forgot password feature
    public static class ForgotPasswordRequest {
        @NotBlank(message = "E-post kan ikke være tom")
        private String email;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class ResetPasswordRequest {
        @NotBlank(message = "Token kan ikke være tom")
        private String token;

        @NotBlank(message = "Nytt passord kan ikke være tomt")
        private String newPassword;

        public String getToken() {
            return token;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public String getNewPassword() {
            return newPassword;
        }
        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
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

        Users user = new Users();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTelephone(request.getTelephone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        if (request.getRole() == Role.ARANGOR) {
            Organizer organizer = new Organizer();
            organizer.setName(request.getFirstName() + " " + request.getLastName());
            organizer.setEmail(request.getEmail());
            organizer.setPhone(request.getTelephone());
            organizerRepository.save(organizer);
        }

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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }

        Optional<Users> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            // For security, don't reveal if email exists
            return ResponseEntity.ok(Map.of("message", "Hvis e-posten finnes, vil du motta en tilbakestillingslenke."));
        }

        Users user = userOptional.get();
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        
        userRepository.save(user);

        // TODO: Send email with reset link
        // For now, we'll just return the token (in production, this should be sent via email)
        System.out.println("Reset token for " + user.getEmail() + ": " + resetToken);
        
        return ResponseEntity.ok(Map.of(
            "message", "Hvis e-posten finnes, vil du motta en tilbakestillingslenke.",
            "token", resetToken // Remove this in production
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("message", errorMsg));
        }

        Optional<Users> userOptional = userRepository.findByResetToken(request.getToken());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ugyldig eller utløpt token"));
        }

        Users user = userOptional.get();
        
        // Check if token is expired
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token har utløpt"));
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Passord har blitt tilbakestilt"));
    }
}
