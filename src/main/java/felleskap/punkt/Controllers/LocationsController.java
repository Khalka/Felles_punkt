package felleskap.punkt.Controllers;

import felleskap.punkt.entity.Locations;
import felleskap.punkt.Repository.LocationsRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationsController {

    private final LocationsRepository repository;

    public LocationsController(LocationsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Locations> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Locations getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public Locations create(@RequestBody Locations location) {
        return repository.save(location);
    }

    @PutMapping("/{id}")
    public Locations update(@PathVariable Long id, @RequestBody Locations updated) {
        updated.setId(id);
        return repository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
