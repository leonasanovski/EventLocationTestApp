package mk.finki.ukim.mk.lab.service;

import mk.finki.ukim.mk.lab.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventService {
    List<Event> listAll();
    List<Event> searchEvents(String text);
    void save_event(Long id, String name, String description, double popularityScore, Long locationID);
    void delete(Long id);
    Optional<Event> findEvent(Long id);

}
