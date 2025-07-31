package felleskap.punkt.Controllers;

import felleskap.punkt.entity.Participants;
import felleskap.punkt.Repository.ParticipantsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
public class ParticipantsController {

    private final ParticipantsRepository repository;

    public ParticipantsController(ParticipantsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Participants> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Participants getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public Participants create(@RequestBody Participants participant) {
        return repository.save(participant);
    }

    @PutMapping("/{id}")
    public Participants update(@PathVariable Long id, @RequestBody Participants updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
