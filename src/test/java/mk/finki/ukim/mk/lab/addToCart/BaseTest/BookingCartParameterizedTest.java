package mk.finki.ukim.mk.lab.addToCart.BaseTest;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.*;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.BookingCartServiceImplementation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;



import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingCartParameterizedTest {

    private final EventService eventService = mock(EventService.class);
    private final BookingCartServiceImplementation service = new BookingCartServiceImplementation(eventService);



    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\n", "\t"})
    void test_blankSelectedEvent_shouldThrow(String eventName) {
        HttpSession session = mock(HttpSession.class);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart(eventName, 1, "user", "address", session));
        assertEquals("Selected event must not be null or blank.", ex.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -5})
    void test_invalidTickets_shouldThrow(int numTickets) {
        HttpSession session = mock(HttpSession.class);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart("event", numTickets, "user", "address", session));
        assertEquals("Number of tickets must be greater than 0.", ex.getMessage());
    }



    @ParameterizedTest
    @CsvSource({
            ",1,user,address,Selected event must not be null or blank.",
            "event,0,user,address,Number of tickets must be greater than 0.",
            "event,1,,address,Username must not be null or blank.",
            "event,1,user,,Address must not be null or blank."
    })
    void test_invalidInputs_csvSource(String event, Integer tickets, String user, String address, String expectedMessage) {
        HttpSession session = mock(HttpSession.class);
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                service.addToCart(event, tickets, user, address, session));
        assertEquals(expectedMessage, ex.getMessage());
    }



    @ParameterizedTest
    @MethodSource("validBookingData")
    void test_validBooking(String selectedEvent, int tickets, double discount, double expectedTotal) {
        HttpSession session = mock(HttpSession.class);
        Event event = mock(Event.class);

        when(eventService.findByName(selectedEvent)).thenReturn(event);
        when(event.getId()).thenReturn(1L);
        when(eventService.countTicketsBookedForEvent(1L)).thenReturn(5);
        when(session.getAttribute("discount")).thenReturn(discount);
        when(event.calculatePrice(eq(tickets), eq(5), any())).thenReturn(100.0);
        when(session.getAttribute("cart")).thenReturn(null);

        BookingCart cart = service.addToCart(selectedEvent, tickets, "Alice", "Street", session);

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals(expectedTotal, cart.getItems().get(0).getTotalPrice());
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> validBookingData() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("Concert", 1, 0.0, 100.0),
                org.junit.jupiter.params.provider.Arguments.of("Concert", 1, 10.0, 90.0),
                org.junit.jupiter.params.provider.Arguments.of("Concert", 1, 25.0, 75.0)
        );
    }
}

