package mk.finki.ukim.mk.lab.service.implementation;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventServiceImplementation implements EventService {
    public final EventRepository eventRepository;
    public final LocationRepository locationRepository;
    private final EventBookingRepository eventBookingRepository;

    public EventServiceImplementation(EventRepository eventRepository, LocationRepository locationRepository, EventBookingRepository eventBookingRepository) {
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.eventBookingRepository = eventBookingRepository;
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
    //GORJAN
    @Override
    public void save_event(Long id, String name, String description, double popularityScore,
                           Long locationID, LocalDateTime from, LocalDateTime to,
                           double basePrice, int maxTickets) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Event name must not be null or blank.");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Event description must not be null or blank.");
        }

        if (popularityScore < 0 || popularityScore > 10) {
            throw new IllegalArgumentException("Popularity score must be between 0 and 10.");
        }

        if (locationID == null) {
            throw new IllegalArgumentException("Location ID must not be null.");
        }

        if (from == null || to == null) {
            throw new IllegalArgumentException("Start and end date must not be null.");
        }

        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        if (basePrice < 0) {
            throw new IllegalArgumentException("Base price must not be negative.");
        }

        if (maxTickets <= 0) {
            throw new IllegalArgumentException("Maximum tickets must be greater than 0.");
        }

        Location location = locationRepository.findById(locationID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid location ID"));

        Event event;
        if (id != null && eventRepository.findById(id).isPresent()) {
            event = eventRepository.findById(id).get();
            event.setName(name);
            event.setDescription(description);
            event.setPopularityScore(popularityScore);
            event.setLocation(location);
            event.setStartTime(from);
            event.setEndTime(to);
            event.setBasePrice(basePrice);
            event.setMaxTickets(maxTickets);
        } else {
            event = new Event(name, description, popularityScore, location, from, to, basePrice, maxTickets);
        }

        if (!isConflict(event)) {
            this.eventRepository.save(event);
        }
    }

    @Override
    public void delete(Long id) {
        this.eventRepository.deleteById(id);
    }

    @Override
    public Optional<Event> findEvent(Long id) {
        return eventRepository.findById(id);
    }
    //LEON
    @Override
    public boolean isConflict(Event newEvent) {
        List<Event> existingEvents = eventRepository.findByLocationId(newEvent.getLocation().getId());
        for (Event existing : existingEvents) {
            boolean overlaps = !newEvent.getEndTime().isBefore(existing.getStartTime()) &&
                    !newEvent.getStartTime().isAfter(existing.getEndTime());
            if (overlaps) return true;
        }
        return false;
    }
    @Override
    public int countTicketsBookedForEvent(Long eventId) {
        return eventBookingRepository.countTicketsByEventId(eventId);
    }
    @Override
    public Event findByName(String name) {
        return eventRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Event with name " + name + " not found"));
    }
    //GORJAN
    @Override
    public Map<Long, Integer> getRemainingTicketsForAllEvents() {
        List<Event> events = eventRepository.findAll();
        Map<Long, Integer> remainingTicketsMap = new HashMap<>();

        for (Event event : events) {
            int booked = eventBookingRepository.countTicketsByEventId(event.getId());
            int remaining = event.getMaxTickets() - booked;
            remainingTicketsMap.put(event.getId(), Math.max(remaining, 0));
        }

        return remainingTicketsMap;
    }
    //LEON
    @Override
    public Map<Long, Double> getDynamicPricesForAllEvents() {
        List<Event> events = eventRepository.findAll();
        Map<Long, Double> priceMap = new HashMap<>();
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (event == null || event.getId() == null) {
                continue; // Skip null or malformed events
            }
            int booked = eventBookingRepository.countTicketsByEventId(event.getId());
            double price;
            if (booked >= event.getMaxTickets()) {
                price = -1.0; // Sold out
            } else {
                price = event.calculatePrice(1, booked, LocalDateTime.now());
            }
            priceMap.put(event.getId(), price);
        }
        return priceMap;
    }
    //LEON
    @Override
    public boolean isEventForAdultsOnly(Event event) {
        double average_max_capacity = eventRepository
                .findAll()
                .stream()
                .mapToDouble(Event::getMaxTickets)
                .average().getAsDouble();

        boolean isBigEvent = event.getMaxTickets() > average_max_capacity;
        boolean isBadRated = event.getPopularityScore() < 3;
        boolean isAtNight = event.getStartTime().getHour() >= 22;
        boolean isLocationOverused = event.getLocation().getEvents().size() > 2;
        return (isBigEvent || isLocationOverused) && !isBadRated && isAtNight;
    }
}
