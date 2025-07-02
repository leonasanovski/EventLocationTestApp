package mk.finki.ukim.mk.lab.bootstrap;

import jakarta.annotation.PostConstruct;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import org.springframework.stereotype.Component;

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
        locations.add(new Location("Capitol Mall","Jane Sandanski","1000 lugje","Mall za shopping"));
        locations.add(new Location("Kej na vardar","Reka Vardar", "inf","Za uzivanje"));
        locations.add(new Location("FINKI kampus","Ul:Rugjer Boskovikj","2000","Studeiranje"));
        locations.add(new Location("Krug Kafe","Jane Sandanski","40","Zabava so prijatelite"));
        this.locationRepository.saveAll(locations);

        events.add(new Event("Tech Conference", "Annual conference covering latest tech trends and innovations.", 7.8,locations.get(2)));
        events.add(new Event("Art Exhibition", "Showcasing modern art from local and international artists.", 6.4,locations.get(3)));
        events.add(new Event("Food Carnival", "A variety of food stalls offering cuisines from around the world.", 8.1,locations.get(0)));
        events.add(new Event("Film Screening", "An outdoor movie night featuring classic films.", 5.7,locations.get(0)));
        events.add(new Event("Book Fair", "A gathering of book lovers with book signings and readings.", 6.9,locations.get(2)));
        events.add(new Event("Science Workshop", "Interactive science sessions for all ages.", 7.3,locations.get(2)));
        events.add(new Event("Charity Run", "A 5K run to raise funds for local charities.", 7.0,locations.get(1)));
        events.add(new Event("Dance Performance", "Live dance performance by well-known choreographers.", 6.6, locations.get(0)));
        events.add(new Event("Historical Tour", "Guided tour of local historical landmarks.", 5.5, locations.get(1)));
        this.eventRepository.saveAll(events);
    }

}
