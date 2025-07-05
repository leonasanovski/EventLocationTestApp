package mk.finki.ukim.mk.lab.addToCart.GC.ADP;



import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.BookingCart;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.BookingCartServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingCartServiceTestDU {

    private BookingCartServiceImplementation service;
    private EventService eventService;

    @BeforeEach
    void setup() {
        eventService = mock(EventService.class);
        service = new BookingCartServiceImplementation(eventService);
    }


    @Test
    void test_eventNotFound_DUPathA() {
        HttpSession session = mock(HttpSession.class);
        when(eventService.findByName("Concert")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("Concert", 2, "Alice", "Wonderland", session));

        assertEquals("Event not found: Concert", ex.getMessage());
    }


    @Test
    void test_validBooking_DUPathB() {
        HttpSession session = mock(HttpSession.class);
        Event event = mock(Event.class);

        when(eventService.findByName("Festival")).thenReturn(event);
        when(event.getId()).thenReturn(1L);
        when(eventService.countTicketsBookedForEvent(1L)).thenReturn(5);
        when(session.getAttribute("discount")).thenReturn(10.0);
        when(event.calculatePrice(eq(2), eq(5), any())).thenReturn(200.0);
        when(session.getAttribute("cart")).thenReturn(null);

        BookingCart cart = service.addToCart("Festival", 2, "Bob", "Magic St", session);

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(180.0, cart.getItems().get(0).getTotalPrice()); // 200 * 0.9
    }
}