package mk.finki.ukim.mk.lab.isEventForAdultsOnly.logic_coverage;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsEventForAdultsOnlyLogicCoverageRACCTests {

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

    // Row 7: T T F T => isBigEvent=true, isLocationOverused=true, isBadRated=false, isAtNight=true => Expect: true
    @Test
    @DisplayName("Row 7: T T F T -> T")
    void testRow7_RACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(50), createEvent(100))); // avg=75
        event.setMaxTickets(100); // > avg => isBigEvent = true
        event.setPopularityScore(4.0); // >=3 => isBadRated = false
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 22, 0)); // >=22 => isAtNight = true
        location.setEvents(List.of(new Event(), new Event(), new Event())); // >2 => isLocationOverused = true

        assertTrue(eventService.isEventForAdultsOnly(event));
    }

    // Row 8: T F F F => Expect: false
    @Test
    @DisplayName("Row 8: T F F F -> F")
    void testRow8_RACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(100), createEvent(200))); // avg = 150
        event.setMaxTickets(160); // big event
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 20, 0)); // not night
        location.setEvents(List.of(new Event())); // not overused

        assertFalse(eventService.isEventForAdultsOnly(event));
    }

    // Row 9: F T F F => isBigEvent=false, locationOverused=true, notBadRated=true, notAtNight=false => Expect: false
    @Test
    @DisplayName("Row 9: F T F F -> F")
    void testRow9_RACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(200), createEvent(150))); // avg = 175
        event.setMaxTickets(100); // not big
        event.setPopularityScore(4.0); // not bad
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 18, 0)); // not night
        location.setEvents(List.of(new Event(), new Event(), new Event())); // overused

        assertFalse(eventService.isEventForAdultsOnly(event));
    }

    // Row 11: T F F T => isBigEvent=true, locationOverused=false, notBadRated=false, notAtNight=true => Expect: true
    @Test
    @DisplayName("Row 11: T F F T -> T")
    void testRow11_RACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(80), createEvent(90))); // avg=85
        event.setMaxTickets(100);
        event.setPopularityScore(4.0);
        event.setStartTime(LocalDateTime.of(2025, 1, 1, 23, 0)); // night
        location.setEvents(List.of(new Event()));
        assertTrue(eventService.isEventForAdultsOnly(event));
    }

    // Row 15: F T F T => isBigEvent=false, isLocationOverused=true, isBadRated=false, isAtNight=true => Expect: true
    @Test
    @DisplayName("Row 15: F T F T -> T")
    void testRow15_RACC() {
        when(eventRepository.findAll()).thenReturn(List.of(createEvent(100), createEvent(150))); // avg=125
        event.setMaxTickets(100); // < avg => isBigEvent = false
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