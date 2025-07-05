package mk.finki.ukim.mk.lab.save_event.GC.ADP;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

class EventServiceDUNameTest {

    private final EventRepository eventRepository = mock(EventRepository.class);
    private final LocationRepository locationRepository = mock(LocationRepository.class);
    private final EventBookingRepository eventBookingRepository = mock(EventBookingRepository.class);
    private final EventServiceImplementation service = new EventServiceImplementation(eventRepository, locationRepository,eventBookingRepository);

    private final Location location = new Location(); // mock or real instance

    // Path A: [0,1,2] → name is null
    @Test
    void testPathA_nameIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.save_event(1L, null, "desc", 5.0, 1L,
                        LocalDateTime.now(), LocalDateTime.now().plusDays(1), 100.0, 50)
        );
        assertEquals("Event name must not be null or blank.", ex.getMessage());
    }

    // Path B: [0,1,3,5,7,9,11,13,14] → update existing event
    @Test
    void testPathB_updateEvent_validName() {
        Event event = new Event();
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        service.save_event(1L, "TestName", "desc", 5.0, 1L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 100.0, 50);

        verify(eventRepository).save(event);
        assertEquals("TestName", event.getName());
    }

    // Path C: [0,1,3,5,7,9,11,13,15,16] → create new event
    @Test
    void testPathC_createNewEvent_validName() {
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        service.save_event(1L, "NewEvent", "desc", 5.0, 1L,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1), 100.0, 50);

        verify(eventRepository).save(any(Event.class));
    }
}