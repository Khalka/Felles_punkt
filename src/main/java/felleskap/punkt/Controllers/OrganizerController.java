package felleskap.punkt.Controllers;

import felleskap.punkt.entity.Organizer;
import felleskap.punkt.Repository.OrganizerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    private final OrganizerRepository repository;

    public OrganizerController(OrganizerRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Organizer> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Organizer getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public Organizer create(@RequestBody Organizer organizer) {
        return repository.save(organizer);
    }

    @PutMapping("/{id}")
    public Organizer update(@PathVariable Long id, @RequestBody Organizer updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
