package mk.finki.ukim.mk.lab.addToCart.GC.PP;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.*;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.BookingCartServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingCartServiceTestPP {

    private BookingCartServiceImplementation service;
    private EventService eventService;
    private Event event;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        service = new BookingCartServiceImplementation(eventService);
        event = mock(Event.class);
        session = mock(HttpSession.class);
    }

    // 1. Valid input
    @Test
    void test_validBooking() {
        when(eventService.findByName("Concert")).thenReturn(event);
        when(eventService.countTicketsBookedForEvent(anyLong())).thenReturn(5);
        when(session.getAttribute("discount")).thenReturn(10.0);
        when(session.getAttribute("cart")).thenReturn(null);
        when(event.getId()).thenReturn(1L);
        when(event.calculatePrice(2, 5, LocalDateTime.now())).thenReturn(200.0); // can use any()

        BookingCart result = service.addToCart("Concert", 2, "Alice", "Street 1", session);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    // 2. selectedEvent is null
    @Test
    void test_nullSelectedEvent() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart(null, 2, "Alice", "Street", session));
        assertEquals("Selected event must not be null or blank.", ex.getMessage());
    }

    // 3. username is blank
    @Test
    void test_blankUsername() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 2, "   ", "Street", session));
        assertEquals("Username must not be null or blank.", ex.getMessage());
    }

    // 4. address is null
    @Test
    void test_nullAddress() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 2, "Alice", null, session));
        assertEquals("Address must not be null or blank.", ex.getMessage());
    }

    // 5. numTickets is zero
    @Test
    void test_zeroTickets() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 0, "Alice", "Street", session));
        assertEquals("Number of tickets must be greater than 0.", ex.getMessage());
    }

    // 6. session is null
    @Test
    void test_nullSession() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 1, "Alice", "Street", null));
        assertEquals("Session must not be null.", ex.getMessage());
    }

    // 7. event not found
    @Test
    void test_eventNotFound() {
        when(eventService.findByName("Unknown")).thenReturn(null);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Unknown", 1, "Alice", "Street", session));
        assertEquals("Event not found: Unknown", ex.getMessage());
    }

    // 8. discount is not a Double
    @Test
    void test_discountInvalidType() {
        when(eventService.findByName("Concert")).thenReturn(event);
        when(eventService.countTicketsBookedForEvent(anyLong())).thenReturn(0);
        when(event.getId()).thenReturn(1L);
        when(session.getAttribute("discount")).thenReturn("invalid");

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 1, "Alice", "Street", session));
        assertEquals("Discount in session must be a valid Double.", ex.getMessage());
    }

    // 9. cart exists already
    @Test
    void test_cartAlreadyExists() {
        BookingCart cart = new BookingCart();
        cart.setItems(new ArrayList<>());

        when(eventService.findByName("Concert")).thenReturn(event);
        when(eventService.countTicketsBookedForEvent(anyLong())).thenReturn(5);
        when(session.getAttribute("discount")).thenReturn(10.0);
        when(session.getAttribute("cart")).thenReturn(cart);
        when(event.getId()).thenReturn(1L);
        when(event.calculatePrice(anyInt(), anyInt(), any())).thenReturn(200.0);

        BookingCart result = service.addToCart("Concert", 1, "Alice", "Street", session);
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
    }

    // 10. discount is 0%
    @Test
    void test_zeroDiscount() {
        when(eventService.findByName("Concert")).thenReturn(event);
        when(eventService.countTicketsBookedForEvent(anyLong())).thenReturn(0);
        when(session.getAttribute("discount")).thenReturn(0.0);
        when(session.getAttribute("cart")).thenReturn(null);
        when(event.getId()).thenReturn(1L);
        when(event.calculatePrice(anyInt(), anyInt(), any())).thenReturn(100.0);

        BookingCart result = service.addToCart("Concert", 1, "Alice", "Street", session);
        assertNotNull(result);
        assertEquals(100.0, result.getItems().get(0).getTotalPrice());
    }

    // 11. discount is 100%
    @Test
    void test_fullDiscount() {
        when(eventService.findByName("Concert")).thenReturn(event);
        when(eventService.countTicketsBookedForEvent(anyLong())).thenReturn(0);
        when(session.getAttribute("discount")).thenReturn(100.0);
        when(session.getAttribute("cart")).thenReturn(null);
        when(event.getId()).thenReturn(1L);
        when(event.calculatePrice(anyInt(), anyInt(), any())).thenReturn(100.0);

        BookingCart result = service.addToCart("Concert", 1, "Alice", "Street", session);
        assertEquals(0.0, result.getItems().get(0).getTotalPrice());
    }
}