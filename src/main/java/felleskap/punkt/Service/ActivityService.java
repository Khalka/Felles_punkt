package felleskap.punkt.Service;

import felleskap.punkt.dto.ActivityDto;
import felleskap.punkt.entity.Activity;
import felleskap.punkt.entity.Locations;
import felleskap.punkt.entity.Organizer;
import felleskap.punkt.entity.Users;
import felleskap.punkt.Repository.ActivityRepository;
import felleskap.punkt.Repository.LocationsRepository;
import felleskap.punkt.Repository.OrganizerRepository;
import felleskap.punkt.Repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final OrganizerRepository organizerRepository;
    private final LocationsRepository locationsRepository;
    private final UsersRepository usersRepository;

    public ActivityService(ActivityRepository activityRepository,
                           OrganizerRepository organizerRepository,
                           LocationsRepository locationsRepository,
                           UsersRepository usersRepository) {
        this.activityRepository = activityRepository;
        this.organizerRepository = organizerRepository;
        this.locationsRepository = locationsRepository;
        this.usersRepository = usersRepository;
    }

    public List<Activity> getActivitiesByOrganizerEmail(String email) {
        Optional<Organizer> organizer = organizerRepository.findByEmail(email);
        if (organizer.isEmpty()) {
            return List.of();  // Returner tom liste om arrangør ikke finnes
        }
        return activityRepository.findAll().stream()
                .filter(a -> a.getOrganizer().getId().equals(organizer.get().getId()))
                .collect(Collectors.toList());
    }

    public Activity createActivity(ActivityDto dto, String organizerEmail) {
        Organizer organizer = organizerRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new RuntimeException("Arrangør ikke funnet"));

        Locations location = locationsRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Lokasjon ikke funnet"));

        Activity activity = new Activity();
        activity.setActivityType(dto.getActivityType());
        activity.setHoldPlace(dto.getHoldPlace());
        activity.setDescription(dto.getDescription());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(location);
        activity.setOrganizer(organizer);

        return activityRepository.save(activity);
    }

    public Activity updateActivity(Long id, ActivityDto dto, String organizerEmail) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktivitet ikke funnet"));

        if (!activity.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("Ikke tilgang til å endre denne aktiviteten");
        }

        Locations location = locationsRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Lokasjon ikke funnet"));

        activity.setActivityType(dto.getActivityType());
        activity.setHoldPlace(dto.getHoldPlace());
        activity.setDescription(dto.getDescription());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(location);

        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id, String organizerEmail) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktivitet ikke funnet"));

        if (!activity.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException("Ikke tilgang til å slette denne aktiviteten");
        }

        activityRepository.deleteById(id);
    }

    // Ny metode for påmelding
    public void registerForActivity(Long activityId, String userEmail) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Aktivitet ikke funnet"));

        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Bruker ikke funnet"));

        if (activity.getRegisteredUsers().contains(user)) {
            throw new RuntimeException("Bruker er allerede påmeldt denne aktiviteten");
        }

        activity.getRegisteredUsers().add(user);
        activityRepository.save(activity);
    }
}
