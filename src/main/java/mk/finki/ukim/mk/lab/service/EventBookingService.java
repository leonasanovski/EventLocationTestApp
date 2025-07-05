package mk.finki.ukim.mk.lab.service;

import mk.finki.ukim.mk.lab.model.EventBooking;

public interface EventBookingService {
    EventBooking bookEvent(String attendeeName, String attendeeAddress, Long eventId, Long numTickets);

    void saveBooking(EventBooking booking);
}
