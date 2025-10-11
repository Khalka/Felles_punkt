package felleskap.punkt.security.user;

import felleskap.punkt.entity.Users;
import felleskap.punkt.Repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    
    private final UsersRepository usersRepository;

    @Autowired
    public UserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("[v0] ========== AUTHENTICATION ATTEMPT ==========");
        System.out.println("[v0] Looking up user with email: " + email);
        System.out.println("[v0] Total users in database: " + usersRepository.count());
        
        usersRepository.findAll().forEach(u -> {
            System.out.println("[v0] Found user - ID: " + u.getUserId() + ", Email: '" + u.getEmail() + "', Role: " + u.getRole());
        });
        
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("[v0] ERROR: User not found with email: " + email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        System.out.println("[v0] SUCCESS: Found user with email: " + user.getEmail() + ", Role: " + user.getRole());
        System.out.println("[v0] ========== AUTHENTICATION SUCCESS ==========");
        
        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
