package felleskap.punkt.Controllers;

import felleskap.punkt.entity.PostAddress;
import felleskap.punkt.Repository.PostAddressRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post-addresses")
public class PostAddressController {

    private final PostAddressRepository repository;

    public PostAddressController(PostAddressRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PostAddress> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public PostAddress getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public PostAddress create(@RequestBody PostAddress address) {
        return repository.save(address);
    }

    @PutMapping("/{id}")
    public PostAddress update(@PathVariable Long id, @RequestBody PostAddress updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
