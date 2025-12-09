package felleskap.punkt.Controllers;

import felleskap.punkt.Service.ActivityService;
import felleskap.punkt.dto.ActivityDto;
import felleskap.punkt.entity.Activity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    // Nytt endepunkt for å hente alle aktiviteter (for USER rolle)
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Activity>> getMyActivities(Authentication authentication) {
        String email = authentication.getName();
        // For ARANGOR: get activities they organize
        // For USER: get activities they are registered for
        List<Activity> activities = activityService.getMyActivities(email);
        return ResponseEntity.ok(activities);
    }

    // Opprett ny aktivitet
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody ActivityDto activityDto, Authentication authentication) {
        String email = authentication.getName();
        Activity savedActivity = activityService.createActivity(activityDto, email);
        return ResponseEntity.ok(savedActivity);
    }

    // Oppdater aktivitet
    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id,
                                                   @RequestBody ActivityDto activityDto,
                                                   Authentication authentication) {
        String email = authentication.getName();
        Activity updatedActivity = activityService.updateActivity(id, activityDto, email);
        return ResponseEntity.ok(updatedActivity);
    }

    // Slett aktivitet
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        activityService.deleteActivity(id, email);
        return ResponseEntity.ok().build();
    }

    // Nytt endepunkt for påmelding
    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerForActivity(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        activityService.registerForActivity(id, email);
        return ResponseEntity.ok(Map.of("message", "Påmelding vellykket"));
    }

    @DeleteMapping("/{id}/register")
    public ResponseEntity<?> unregisterForActivity(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        activityService.unregisterForActivity(id, email);
        return ResponseEntity.ok(Map.of("message", "Avmelding vellykket"));
    }
}
