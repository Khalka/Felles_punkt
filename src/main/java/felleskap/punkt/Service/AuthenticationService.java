package felleskap.punkt.Service;

import felleskap.punkt.dto.AuthenticationRequest;
import felleskap.punkt.dto.AuthenticationResponse;
import felleskap.punkt.dto.RegisterRequest;
import felleskap.punkt.entity.Users;
import felleskap.punkt.Domain.Role;
import felleskap.punkt.Repository.UsersRepository;
import felleskap.punkt.Util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
/*@RequiredArgsConstructor*/
public class AuthenticationService {

    private final AuthenticationManager authManager;
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public AuthenticationService(AuthenticationManager authManager,
                                 UsersRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtUtil jwtUtil,
                                 UserDetailsService userDetailsService) {
        this.authManager = authManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    
    public AuthenticationResponse register(RegisterRequest request) {
        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Konverter string til enum Role
       user.setRole(Role.valueOf(request.getRole().name()));

        // Her kan du sette flere felt om du vil, f.eks. navn, telefon, adresse

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(user.getUsername(),
                user.getAuthorities().stream().findFirst().get().getAuthority());
        return new AuthenticationResponse(token);
    }
}
