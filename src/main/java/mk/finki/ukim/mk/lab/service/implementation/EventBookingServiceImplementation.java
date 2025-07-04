package mk.finki.ukim.mk.lab.service.implementation;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.EventBookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EventBookingServiceImplementation implements EventBookingService {

    private final EventRepository eventRepository;
    private final EventBookingRepository eventBookingRepository;

    public EventBookingServiceImplementation(EventRepository eventRepository, EventBookingRepository eventBookingRepository) {
        this.eventRepository = eventRepository;
        this.eventBookingRepository = eventBookingRepository;
    }

    //LEON
    @Override
    public EventBooking bookEvent(String attendeeName, String attendeeAddress,
                                  Long eventId, Long numTickets) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        int alreadyBooked = eventBookingRepository.countTicketsByEventId(eventId);

        double totalPrice = event.calculatePrice(numTickets.intValue(), alreadyBooked, LocalDateTime.now());

        EventBooking booking = new EventBooking(event, attendeeName, attendeeAddress, numTickets, totalPrice);
        return eventBookingRepository.save(booking);
    }
    @Override
    public void saveBooking(EventBooking booking) {
        eventBookingRepository.save(booking);
    }

}
