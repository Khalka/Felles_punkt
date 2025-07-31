package felleskap.punkt.Controllers;

import felleskap.punkt.entity.Users;
import felleskap.punkt.Repository.UsersRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
