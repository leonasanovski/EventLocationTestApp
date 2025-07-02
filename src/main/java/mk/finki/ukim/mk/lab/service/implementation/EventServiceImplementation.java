package mk.finki.ukim.mk.lab.service.implementation;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImplementation implements EventService {
    public final EventRepository eventRepository;
    public final LocationRepository locationRepository;

    public EventServiceImplementation(EventRepository eventRepository, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> searchEvents(String text) {
        if (text.isEmpty()) throw new IllegalArgumentException("I cant search without any text!");
        return eventRepository.findByDescription(text);
    }

    @Override
    public void save_event(Long id, String name, String description, double popularityScore, Long locationID) {
        Location location = locationRepository.findById(locationID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));
        Event event;
        if (id != null && eventRepository.findById(id).isPresent()) {
            event = eventRepository.findById(id).get();
            // Update existing event
            event.setName(name);
            event.setDescription(description);
            event.setPopularityScore(popularityScore);
            event.setLocation(location);
        } else {
            // Create a new event
            event = new Event(name, description, popularityScore, location);
        }
        this.eventRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        this.eventRepository.deleteById(id);
    }

    @Override
    public Optional<Event> findEvent(Long id) {
        return eventRepository.findById(id);
    }
}
