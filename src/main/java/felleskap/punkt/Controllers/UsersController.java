package felleskap.punkt.Controllers;

import felleskap.punkt.Domain.Role;
import felleskap.punkt.entity.Users;
import felleskap.punkt.Repository.UsersRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public List<Users> getAll() {
        return usersRepository.findAll();
    }

    @GetMapping("/{id}")
    public Users getById(@PathVariable Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Users create(@RequestBody Users user) {
        return usersRepository.save(user);
    }

    @PutMapping("/{id}")
    public Users update(@PathVariable Long id, @RequestBody Users updated) {
        updated.setUserId(id);
        return usersRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        usersRepository.deleteById(id);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> request) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bruker ikke funnet"));

        String roleStr = request.get("role");
        try {
            Role newRole = Role.valueOf(roleStr);
            user.setRole(newRole);
            usersRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "Rolle oppdatert"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ugyldig rolle"));
        }
    }
}
