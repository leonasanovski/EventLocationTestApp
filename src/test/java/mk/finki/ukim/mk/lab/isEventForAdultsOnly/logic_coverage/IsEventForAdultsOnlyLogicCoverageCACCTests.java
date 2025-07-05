package mk.finki.ukim.mk.lab.isEventForAdultsOnly.logic_coverage;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsEventForAdultsOnlyLogicCoverageCACCTests {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventServiceImplementation eventService;

    private Event event;
    private Location location;

    @BeforeEach
    void setup() {
        event = new Event();
        location = new Location();
        event.setLocation(location);
    }

    // Row 7: T T F T => Expect: true
    @Test
    @DisplayName("Row 7: T T F T -> T")
    void testRow7_CACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(50), createEvent(100))); // avg=75
        event.setMaxTickets(100); // big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 22, 0)); // night
        location.setEvents(List.of(new Event(), new Event(), new Event())); // overused

        assertTrue(eventService.isEventForAdultsOnly(event));
    }

    // Row 9: F T F F => Expect: false
    @Test
    @DisplayName("Row 9: F T F F -> F")
    void testRow9_CACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(150), createEvent(200))); // avg=175
        event.setMaxTickets(100); // not big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 20, 0)); // not night
        location.setEvents(List.of(new Event(), new Event(), new Event())); // overused

        assertFalse(eventService.isEventForAdultsOnly(event));
    }

    // Row 11: T F F T => Expect: true
    @Test
    @DisplayName("Row 11: T F F T -> T")
    void testRow11_CACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(80), createEvent(90))); // avg=85
        event.setMaxTickets(100); // big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 23, 0)); // night
        location.setEvents(List.of(new Event())); // not overused

        assertTrue(eventService.isEventForAdultsOnly(event));
    }

    // Row 12: F F F T => Expect: false
    @Test
    @DisplayName("Row 12: F F F T -> F")
    void testRow12_CACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(150), createEvent(160))); // avg = 155
        event.setMaxTickets(100); // not big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 22, 0)); // night
        location.setEvents(List.of(new Event())); // not overused

        assertFalse(eventService.isEventForAdultsOnly(event));
    }

    // Row 15: F T F T => Expect: true
    @Test
    @DisplayName("Row 15: F T F T -> T")
    void testRow15_CACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(100), createEvent(150))); // avg = 125
        event.setMaxTickets(100); // not big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 22, 30)); // night
        location.setEvents(List.of(new Event(), new Event(), new Event())); // overused

        assertTrue(eventService.isEventForAdultsOnly(event));
    }

    private Event createEvent(int maxTickets) {
        Event dummy = new Event();
        dummy.setMaxTickets(maxTickets);
        return dummy;
    }
}
