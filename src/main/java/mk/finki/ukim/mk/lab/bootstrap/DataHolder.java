package mk.finki.ukim.mk.lab.bootstrap;
import jakarta.annotation.PostConstruct;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataHolder {
    public static List<Event> events = new ArrayList<>();
    public static List<Location> locations = new ArrayList<>();

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    public DataHolder(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @PostConstruct
    public void init() {
        locations.add(new Location("Capitol Mall", "Jane Sandanski", "1000 lugje", "Mall za shopping"));
        locations.add(new Location("Kej na vardar", "Reka Vardar", "inf", "Za uzivanje"));
        locations.add(new Location("FINKI kampus", "Ul:Rugjer Boskovikj", "2000", "Studeiranje"));
        locations.add(new Location("Krug Kafe", "Jane Sandanski", "40", "Zabava so prijatelite"));
        this.locationRepository.saveAll(locations);

        events.add(new Event("Tech Conference", "Annual conference covering latest tech trends and innovations.",
                7.8, locations.get(2),
                LocalDateTime.of(2025, 7, 10, 9, 0),
                LocalDateTime.of(2025, 7, 10, 17, 0),
                120.0, 10));

        events.add(new Event("Art Exhibition", "Showcasing modern art from local and international artists.",
                6.4, locations.get(3),
                LocalDateTime.of(2025, 7, 11, 10, 0),
                LocalDateTime.of(2025, 7, 11, 18, 0),
                80.0, 150));

        events.add(new Event("Food Carnival", "A variety of food stalls offering cuisines from around the world.",
                8.1, locations.get(0),
                LocalDateTime.of(2025, 7, 12, 12, 0),
                LocalDateTime.of(2025, 7, 12, 22, 0),
                50.0, 300));

        events.add(new Event("Film Screening", "An outdoor movie night featuring classic films.",
                5.7, locations.get(0),
                LocalDateTime.of(2025, 7, 13, 20, 0),
                LocalDateTime.of(2025, 7, 13, 23, 0),
                30.0, 200));

        events.add(new Event("Book Fair", "A gathering of book lovers with book signings and readings.",
                6.9, locations.get(2),
                LocalDateTime.of(2025, 7, 14, 9, 0),
                LocalDateTime.of(2025, 7, 14, 16, 0),
                60.0, 250));

        events.add(new Event("Science Workshop", "Interactive science sessions for all ages.",
                7.3, locations.get(2),
                LocalDateTime.of(2025, 7, 15, 10, 0),
                LocalDateTime.of(2025, 7, 15, 15, 0),
                70.0, 120));

        events.add(new Event("Charity Run", "A 5K run to raise funds for local charities.",
                7.0, locations.get(1),
                LocalDateTime.of(2025, 7, 16, 8, 0),
                LocalDateTime.of(2025, 7, 16, 10, 0),
                40.0, 500));

        events.add(new Event("Dance Performance", "Live dance performance by well-known choreographers.",
                6.6, locations.get(0),
                LocalDateTime.of(2025, 7, 17, 19, 0),
                LocalDateTime.of(2025, 7, 17, 21, 0),
                90.0, 180));

        events.add(new Event("Historical Tour", "Guided tour of local historical landmarks.",
                5.5, locations.get(1),
                LocalDateTime.of(2025, 7, 18, 14, 0),
                LocalDateTime.of(2025, 7, 18, 17, 0),
                45.0, 80));
        this.eventRepository.saveAll(events);
    }
}