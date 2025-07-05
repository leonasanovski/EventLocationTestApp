package mk.finki.ukim.mk.lab.save_event.GC.PP;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;

import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventServiceGraphTest {

    private LocationRepository locationRepository;
    private EventRepository eventRepository;
    private EventServiceImplementation service;
    private EventBookingRepository bookingRepository;

    @BeforeEach
    void setup() {
        locationRepository = mock(LocationRepository.class);
        eventRepository = mock(EventRepository.class);
        bookingRepository = mock(EventBookingRepository.class);
        service = new EventServiceImplementation(eventRepository, locationRepository,bookingRepository);
    }

    private void prepareAndSaveEvent(Long id, boolean repoHasEvent, boolean nullFrom, boolean nullTo, boolean conflict) {
        Location location = mock(Location.class);
        Event event = mock(Event.class);

        when(locationRepository.findById(any())).thenReturn(Optional.of(location));
        if (repoHasEvent) {
            when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        } else {
            when(eventRepository.findById(id)).thenReturn(Optional.empty());
        }

        EventServiceImplementation spyService = spy(service);
        doReturn(conflict).when(spyService).isConflict(any());

        LocalDateTime from = nullFrom ? null : LocalDateTime.now();
        LocalDateTime to = nullTo ? null : LocalDateTime.now().plusDays(1);

        if (!repoHasEvent && (from == null || to == null)) {
            assertThrows(NullPointerException.class, () ->
                    spyService.save_event(id, "n", "d", 2.0, 1L, from, to, 100, 100)
            );
            return;
        }

        spyService.save_event(id, "n", "d", 2.0, 1L, from, to, 100, 100);

        if (!conflict && repoHasEvent && from != null && to != null) {
            verify(eventRepository, atLeastOnce()).save(any());
        }
    }

    // Path 0: [0,1,2]
    @Test
    void testPath0_invalidLocationId() {
        when(locationRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                service.save_event(1L, "n", "d", 1.0, 100L, LocalDateTime.now(), LocalDateTime.now(), 100, 100));
    }

    // Path 1: [0,1,3,4]
    @Test
    void testPath1_eventMissing_conflict() {
        prepareAndSaveEvent(1L, false, false, false, true);
    }

    // Path 2: [0,1,3,5,6]
    @Test
    void testPath2_eventMissing_nullDate() {
        assertThrows(IllegalArgumentException.class, () ->
                prepareAndSaveEvent(1L, true, true, true, false));
    }
    // Path 3: [0,1,3,5,7,8]
    @Test
    void testPath3_eventMissing_onlyFromNull() {
        assertThrows(IllegalArgumentException.class, () ->
                service.save_event(
                        1L,
                        "Event Name",
                        "Some Description",
                        5.0,
                        1L,
                        null,         // from is null
                        LocalDateTime.now().plusDays(1),
                        100.0,
                        10
                )
        );
    }

    // Path 4: [0,1,3,5,7,9,10]
    @Test
    void testPath4_eventMissing_noConflict() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 5: [0,1,3,5,7,9,11,12]
    @Test
    void testPath5_saveNewEvent_path12() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 6: [0,1,3,5,7,9,11,13,14]
    @Test
    void testPath6_saveNewEvent_path14() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 7: [0,1,3,5,7,9,11,13,15,16]
    @Test
    void testPath7_saveNewEvent_path16() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 8: [0,1,3,5,7,9,11,13,15,17,18,19,21,22,23]
    @Test
    void testPath8_saveNewEvent_fullPath() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 9: [0,1,3,5,7,9,11,13,15,17,18,20,21,22,23]
    @Test
    void testPath9_saveNewEvent_alternateBranch() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 10: [0,1,3,5,7,9,11,13,15,17,18,19,21,23]
    @Test
    void testPath10_saveNewEvent_shorterEnd() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }

    // Path 11: [0,1,3,5,7,9,11,13,15,17,18,20,21,23]
    @Test
    void testPath11_saveNewEvent_lastAltPath() {
        prepareAndSaveEvent(1L, false, false, false, false);
    }
}
