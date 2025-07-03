package mk.finki.ukim.mk.lab.service.implementation;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils;
import mk.finki.ukim.mk.lab.model.BookingCart;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.service.BookingCartService;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.LoginService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingCartServiceImplementation implements BookingCartService {

    private final EventService eventService;
    public BookingCartServiceImplementation(EventService eventService) {
        this.eventService = eventService;

    }

    @Override
    public BookingCart addToCart(String selectedEvent, int numTickets, String username, String address, HttpSession session) {
        Event event = eventService.findByName(selectedEvent);
        int alreadyBooked = eventService.countTicketsBookedForEvent(event.getId());

        double loyaltyDiscount = (double) session.getAttribute("discount");
        double basePrice = event.calculatePrice(numTickets, alreadyBooked, LocalDateTime.now());

        double totalPrice = basePrice * (1 - loyaltyDiscount/100);

        BookingCart cart = (BookingCart) session.getAttribute("cart");
        if (cart == null) {
            cart = new BookingCart();
            cart.setAttendeeName(username);
            cart.setAddress(address);
        }

        EventBooking booking = new EventBooking(event, username, address, (long) numTickets, totalPrice);
        cart.getItems().add(booking);

        return cart;
    }
}
