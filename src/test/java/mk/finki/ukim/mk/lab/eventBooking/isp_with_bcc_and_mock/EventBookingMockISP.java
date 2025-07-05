package mk.finki.ukim.mk.lab.eventBooking.isp_with_bcc_and_mock;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventBookingServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventBookingMockISP {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventBookingRepository eventBookingRepository;

    @InjectMocks
    private EventBookingServiceImplementation eventBookingService;

    private Event event;

    @BeforeEach
    void setup() {
        event = new Event(100.0, 100, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(3));
    }

    @Test
    @DisplayName("TC1 - All valid inputs")
    void test_TC1_ValidBookingCreated() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(10);
        when(eventBookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EventBooking booking = eventBookingService.bookEvent("John", "Main Street", 1L, 2L);
        assertNotNull(booking);
        assertEquals("John", booking.getAttendeeName());
    }


    @Test
    @DisplayName("TC2 - attendeeName is null")
    void test_TC2_AttendeeNameNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent(null, "Main Street", 1L, 2L));
        assertEquals("Attendee name cannot be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("TC3 - attendeeName is empty")
    void test_TC3_AttendeeNameEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("  ", "Main Street", 1L, 2L));
        assertEquals("Attendee name cannot be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("TC4 - attendeeAddress is empty")
    void test_TC4_AttendeeAddressEmpty() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", "", 1L, 2L));
        assertEquals("Attendee address cannot be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("TC4 - attendeeAddress is null")
    void test_TC5_AttendeeAddressNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", null, 1L, 2L));
        assertEquals("Attendee address cannot be null or empty.", ex.getMessage());
    }

    @Test
    @DisplayName("TC6 - eventId is null")
    void test_TC6_EventIdNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", "Main Street", null, 2L));
        assertEquals("Event ID cannot be null.", ex.getMessage());
    }

    @Test
    @DisplayName("TC7 - event not found")
    void test_TC7_EventNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                eventBookingService.bookEvent("John", "Main Street", 999L, 2L));
        assertEquals("Event not found", ex.getMessage());
    }

    @Test
    @DisplayName("TC8 - numTickets is null")
    void test_TC8_NumTicketsNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", "Main Street", 1L, null));
        assertEquals("Number of tickets must be a positive number.", ex.getMessage());
    }

    @Test
    @DisplayName("TC9 - numTickets is 0")
    void test_TC9_NumTicketsZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", "Main Street", 1L, 0L));
        assertEquals("Number of tickets must be a positive number.", ex.getMessage());
    }

    @Test
    @DisplayName("TC10 - overflow is considered invalid manually")
    void test_TC10_NumTicketsOverflow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(10);
        assertThrows(IllegalArgumentException.class, () ->
                eventBookingService.bookEvent("John", "Main Street", 1L, Long.MAX_VALUE));
    }

    @Test
    @DisplayName("TC11 - booking created but price is 0.0")
    void test_TC11_TotalPriceZero() {
        Event freeEvent = new Event(0.0, 100, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(10).plusHours(3));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(freeEvent));
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(10);
        when(eventBookingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        EventBooking booking = eventBookingService.bookEvent("John", "Main Street", 1L, 2L);
        assertEquals(0.0, booking.getTotalPrice());
    }
}
