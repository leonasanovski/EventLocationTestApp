package mk.finki.ukim.mk.lab.addToCart.ISP;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.*;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.BookingCartServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class BookingCartServiceTest {

    private BookingCartServiceImplementation service;
    private Event sampleEvent;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        service = new BookingCartServiceImplementation(new DummyEventService());

        session = new DummySession();
        session.setAttribute("discount", 10.0);
        session.setAttribute("cart", null);

        Location location = new Location();
        LocalDateTime now = LocalDateTime.now();
        sampleEvent = new Event("Concert", "Description", 5.0, location, now.plusDays(10), now.plusDays(10).plusHours(2), 100.0, 100);
        DummyEventService.addEvent(sampleEvent);
    }

    @Nested
    class DummyEventService implements EventService {

        private static final Map<String, Event> EVENTS = new HashMap<>();

        public static void addEvent(Event event) {
            EVENTS.put(event.getName(), event);
        }

        @Override
        public Event findByName(String name) {
            return EVENTS.get(name);
        }

        @Override
        public Map<Long, Integer> getRemainingTicketsForAllEvents() {
            return Map.of();
        }

        @Override
        public Map<Long, Double> getDynamicPricesForAllEvents() {
            return Map.of();
        }

        @Override
        public boolean isEventForAdultsOnly(Event event) {
            return false;
        }

        @Override
        public List<Event> listAll() {
            return List.of();
        }

        @Override
        public List<Event> searchEvents(String text) {
            return List.of();
        }

        @Override
        public void save_event(Long id, String name, String description, double popularityScore, Long locationID, LocalDateTime from, LocalDateTime to, double basePrice, int maxTickets) {

        }

        @Override
        public void delete(Long id) {

        }

        @Override
        public Optional<Event> findEvent(Long id) {
            return Optional.empty();
        }

        @Override
        public boolean isConflict(Event newEvent) {
            return false;
        }

        @Override
        public int countTicketsBookedForEvent(Long eventId) {
            return 50; // fixed dummy value for tests
        }
    }


    public class DummySession implements HttpSession {

        private final Map<String, Object> attributes = new HashMap<>();

        @Override
        public long getCreationTime() {
            return 0;
        }

        @Override
        public String getId() {
            return "";
        }

        @Override
        public long getLastAccessedTime() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public void setMaxInactiveInterval(int i) {

        }

        @Override
        public int getMaxInactiveInterval() {
            return 0;
        }

        @Override
        public Object getAttribute(String name) {
            return attributes.get(name);
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
            attributes.put(name, value);
        }

        @Override
        public void removeAttribute(String s) {

        }

        @Override
        public void invalidate() {

        }

        @Override
        public boolean isNew() {
            return false;
        }

        // The following are stubbed or no-op since they're not used in the tests


    }


    @Test
    void testValidAddToCart() {
        BookingCart cart = service.addToCart("Concert", 2, "John", "Main St", session);
        assertNotNull(cart);
        assertEquals("John", cart.getAttendeeName());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    void testNullSelectedEvent() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart(null, 2, "John", "Main St", session));
    }

    @Test
    void testBlankUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("Concert", 2, "  ", "Main St", session));
    }

    @Test
    void testNullAddress() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("Concert", 2, "John", null, session));
    }

    @Test
    void testZeroTickets() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("Concert", 0, "John", "Main St", session));
    }

    @Test
    void testNullSession() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("Concert", 2, "John", "Main St", null));
    }

    @Test
    void testEventNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("UnknownEvent", 2, "John", "Main St", session));
    }

    @Test
    void testInvalidDiscountType() {
        session.setAttribute("discount", "notADouble");
        assertThrows(IllegalArgumentException.class,
                () -> service.addToCart("Concert", 2, "John", "Main St", session));
    }
}