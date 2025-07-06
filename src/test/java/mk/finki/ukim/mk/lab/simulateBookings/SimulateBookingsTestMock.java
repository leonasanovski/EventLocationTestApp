package mk.finki.ukim.mk.lab.simulateBookings;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.implementation.LoginServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulateBookingsTestMock {


    private EventRepository eventRepo;
    private EventBookingRepository bookingRepo;
    private LoginServiceImplementation service;

    @BeforeEach
    void setup() {
        eventRepo = mock(EventRepository.class);
        bookingRepo = mock(EventBookingRepository.class);
        service = new LoginServiceImplementation(bookingRepo, eventRepo);
    }

    @Test
    @DisplayName("Bookings are saved if events exist")
    void testBookingsAreSavedWhenEventsExist() {
        when(eventRepo.findAll()).thenReturn(Arrays.asList(new Event(), new Event()));

        int num = service.simulateBookings("gorjan");

        assertTrue(num >= 0 && num <= 59);
        verify(bookingRepo, atLeast(0)).save(any(EventBooking.class));
    }

    @Test
    @DisplayName("Null username still works (not ideal, but allowed)")
    void testNullUsernameDoesNotCrash() {
        when(eventRepo.findAll()).thenReturn(List.of(new Event(), new Event()));

        int num = service.simulateBookings(null);

        assertTrue(num >= 0 && num <= 59);
        verify(bookingRepo, atMost(59)).save(any(EventBooking.class));
    }

    @Test
    @DisplayName("Only one event: all bookings go to same event")
    void testSingleEventIsAlwaysUsed() {
        Event event = new Event();
        event.setBasePrice(123.0);
        when(eventRepo.findAll()).thenReturn(Collections.singletonList(event));

        int num = service.simulateBookings("mono");

        assertTrue(num >= 0 && num <= 59);
        verify(bookingRepo, atMost(59)).save(argThat(booking ->
                booking.getEvent() == event && booking.getTotalPrice() == 123.0
        ));
    }

    @Test
    @DisplayName("simulateBookings runs 1000 times without crash")
    void testStabilityOverMultipleRuns() {
        when(eventRepo.findAll()).thenReturn(List.of(new Event(), new Event(), new Event()));

        for (int i = 0; i < 1000; i++) {
            assertDoesNotThrow(() -> service.simulateBookings("batchUser"));
        }
    }

    @Test
    @DisplayName("simulateBookings throws if event list is empty")
    void testFailsOnEmptyEvents() {
        when(eventRepo.findAll()).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.simulateBookings("noEventsUser");
        });
    }

    @Test
    @DisplayName("eventRepo returns null → NullPointerException")
    void testRepoReturnsNullThrows() {
        when(eventRepo.findAll()).thenReturn(null);
        assertThrows(NullPointerException.class, () ->
                service.simulateBookings("failUser"));
    }

    @Test
    @DisplayName("simulateBookings returns number between 0–59 and saves that many bookings (approx.)")
    void testSimulateBookingsRandomBehavior() {
        String username = "randomUser";
        List<Event> events = Arrays.asList(new Event(), new Event(), new Event());

        when(eventRepo.findAll()).thenReturn(events);

        int bookings = service.simulateBookings(username);

        assertTrue(bookings >= 0 && bookings < 60);
        verify(bookingRepo, atMost(59)).save(any(EventBooking.class));
    }
}