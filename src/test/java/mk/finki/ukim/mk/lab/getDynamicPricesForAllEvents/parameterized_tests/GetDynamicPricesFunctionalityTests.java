package mk.finki.ukim.mk.lab.getDynamicPricesForAllEvents.parameterized_tests;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetDynamicPricesFunctionalityTests {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventBookingRepository eventBookingRepository;

    @InjectMocks
    private EventServiceImplementation eventService;

    @ParameterizedTest
    @CsvSource({
            "1, 200, 16, 50.0",     // Event available - has tickets left
            "2, 50, 50, -1.0",      // Event sold out - exactly at capacity
            "3, 75, 2, 25.0"       // Event with low booking
    })
    @DisplayName("Various event combinations")
    void testSingleEventPricing(Long eventId, int maxTickets, int bookedTickets, double expectedPrice) {
        Event event = mock(Event.class);
        when(event.getId()).thenReturn(eventId);
        when(event.getMaxTickets()).thenReturn(maxTickets);
        when(eventRepository.findAll()).thenReturn(List.of(event));
        when(eventBookingRepository.countTicketsByEventId(eventId)).thenReturn(bookedTickets);
        if (bookedTickets < maxTickets) {
            when(event.calculatePrice(eq(1), eq(bookedTickets), any(LocalDateTime.class))).thenReturn(expectedPrice);
        }
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(1, result.size());
        assertEquals(expectedPrice, result.get(eventId));
    }

    @Test
    @DisplayName("There is no event to calculate the dynamic price from it")
    void testEmptyEventsList() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Null event is skipped")
    void testNullEventSkipped() {
        List<Event> eventsWithNull = Arrays.asList(null, createMockEvent(1L, 100));
        when(eventRepository.findAll()).thenReturn(eventsWithNull);
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(30);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(1, result.size()); // Only non-null event processed
        assertTrue(result.containsKey(1L));
    }

    @Test
    @DisplayName("Existing event with null id provided is skipped")
    void testEventWithNullIdSkipped() {
        Event eventWithNullId = mock(Event.class);
        when(eventWithNullId.getId()).thenReturn(null);
        List<Event> events = Arrays.asList(eventWithNullId, createMockEvent(1L, 100));
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(1L)).thenReturn(30);
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(1, result.size()); // Only event with valid ID processed
        assertTrue(result.containsKey(1L));
    }

    private Event createMockEvent(Long id, int maxTickets) {
        /*Mocks event, mocks event behavior when getId is called and getMaxTickets is called*/
        Event event = mock(Event.class);
        when(event.getId()).thenReturn(id);
        when(event.getMaxTickets()).thenReturn(maxTickets);
        return event;
    }

}