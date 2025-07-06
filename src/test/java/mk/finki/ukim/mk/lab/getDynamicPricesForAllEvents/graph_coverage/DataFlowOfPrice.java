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

class DataFlowOfPrice {

    private final EventRepository eventRepository = mock(EventRepository.class);
    private final EventBookingRepository eventBookingRepository = mock(EventBookingRepository.class);
    private final LocationRepository locationRepository = mock(LocationRepository.class);

    private final EventService eventService = new EventServiceImplementation(
            eventRepository, locationRepository, eventBookingRepository
    );

    @Test
    @DisplayName("DU Path 1: price = -1.0 → used in priceMap.put()")
    void testPriceSoldOut_DUPath() {
        List<Event> events = new ArrayList<>();
        Event soldOutEvent = new Event(100.0, 50,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10));
        soldOutEvent.setId(201L);
        events.add(soldOutEvent);
        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(201L)).thenReturn(50); // Sold out
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        assertEquals(-1.0, result.get(201L));
    }

    @Test
    @DisplayName("DU Path 2: price = calculatePrice(...) → used in priceMap.put()")
    void testPriceCalculated_DUPath() {
        List<Event> events = new ArrayList<>();
        Event event = new Event(150.0, 100,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(12));
        event.setId(202L);
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);
        when(eventBookingRepository.countTicketsByEventId(202L)).thenReturn(70); // Tickets available
        Map<Long, Double> result = eventService.getDynamicPricesForAllEvents();
        double expected = event.calculatePrice(1, 70, LocalDateTime.now());
        assertEquals(expected, result.get(202L));
    }
}