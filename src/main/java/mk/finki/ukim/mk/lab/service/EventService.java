package mk.finki.ukim.mk.lab.service;

import mk.finki.ukim.mk.lab.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EventService {
    List<Event> listAll();
    List<Event> searchEvents(String text);
    void save_event(Long id, String name, String description, double popularityScore, Long locationID, LocalDateTime from, LocalDateTime to,double basePrice, int maxTickets);
    void delete(Long id);
    Optional<Event> findEvent(Long id);
     boolean isConflict(Event newEvent);
    int countTicketsBookedForEvent(Long eventId);
    Event findByName(String name);
    Map<Long, Integer> getRemainingTicketsForAllEvents();
    Map<Long, Double> getDynamicPricesForAllEvents();
    boolean isEventForAdultsOnly(Event event);

}
