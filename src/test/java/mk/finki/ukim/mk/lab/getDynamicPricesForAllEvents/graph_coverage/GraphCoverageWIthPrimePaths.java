package mk.finki.ukim.mk.lab.getDynamicPricesForAllEvents.graph_coverage;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.LocationRepository;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GraphCoverageWIthPrimePaths {
    private final EventRepository eventRepository = mock(EventRepository.class);
    private final EventBookingRepository eventBookingRepository = mock(EventBookingRepository.class);
    private final EventService eventService = new EventServiceImplementation(eventRepository, mock(LocationRepository.class), eventBookingRepository);

    @Test
    @DisplayName("TC1: Valid Event – Not Sold Out")
    void testValidEventNotSoldOut() {
        List<Event> events = new ArrayList<>();
        Event event1 = new Event(100.0, 100,
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20));
        event1.setId(1L);
        events.add(event1);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(50);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        double expected = event1.calculatePrice(1, 50, LocalDateTime.now());
        assertEquals(expected, result.get(1L));
    }

    @Test
    @DisplayName("TC2: Valid Event – Sold Out")
    void testValidEventSoldOut() {
        List<Event> events = new ArrayList<>();
        Event event2 = new Event(100.0, 100,
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20));
        event2.setId(2L);
        events.add(event2);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(2L)).thenReturn(100);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(-1.0, result.get(2L));
    }

    @Test
    @DisplayName("TC3: Null or Malformed Event")
    void testNullOrMalformedEvent() {
        List<Event> events = new ArrayList<>();
        events.add(null);
        Event event3 = new Event(80.0, 200,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(25));
        event3.setId(3L);
        events.add(event3);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(3L)).thenReturn(150);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        double expected = event3.calculatePrice(1, 150, LocalDateTime.now());
        assertEquals(expected, result.get(3L));
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("TC4: Multiple Events – Sold Out and Not Sold Out")
    void testMultipleEventsSoldOutAndNotSoldOut() {
        List<Event> events = new ArrayList<>();
        Event event4 = new Event(100.0, 100,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(20));
        event4.setId(4L);
        Event event5 = new Event(90.0, 100,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(20));
        event5.setId(5L);
        events.add(event4);
        events.add(event5);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(4L)).thenReturn(100);
        when(eventBookingRepository.countTicketsByEventId(5L)).thenReturn(80);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(-1.0, result.get(4L));
        double expected = event5.calculatePrice(1, 80, LocalDateTime.now());
        assertEquals(expected, result.get(5L));
    }

    @Test
    @DisplayName("TC5: Null + Sold Out + Valid")
    void testNullSoldOutAndValidEvent() {
        List<Event> events = new ArrayList<>();
        events.add(null);
        Event event6 = new Event(110.0, 100,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(15));
        event6.setId(6L);
        Event event7 = new Event(95.0, 120,
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(30));
        event7.setId(7L);
        events.add(event6);
        events.add(event7);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(6L)).thenReturn(100);
        when(eventBookingRepository.countTicketsByEventId(7L)).thenReturn(90);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(-1.0, result.get(6L));
        double expected = event7.calculatePrice(1, 90, LocalDateTime.now());
        assertEquals(expected, result.get(7L));
    }

    @Test
    @DisplayName("TC6: All Types – Edge & Backtrack Paths")
    void testAllTypesEdgeBacktrack() {
        List<Event> events = new ArrayList<>();
        Event eventNullId = new Event(70.0, 50,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(15));
        eventNullId.setId(null);
        Event event8 = new Event(60.0, 10,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(10));
        event8.setId(8L);
        Event eventNullId2 = new Event(80.0, 30,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(8));
        eventNullId2.setId(null);
        events.add(eventNullId);
        events.add(event8);
        events.add(eventNullId2);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(8L)).thenReturn(10);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(-1.0, result.get(8L));
        assertEquals(1, result.size());
    }
}
