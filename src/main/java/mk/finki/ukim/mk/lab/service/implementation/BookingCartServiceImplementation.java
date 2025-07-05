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

    //UNIT TESTS SO @CsvSource I @MethodSource
    //GORJAN
    @Override
    public BookingCart addToCart(String selectedEvent, Integer numTickets, String username, String address, HttpSession session) {
        if (selectedEvent == null || selectedEvent.isBlank()) {
            throw new IllegalArgumentException("Selected event must not be null or blank.");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username must not be null or blank.");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address must not be null or blank.");
        }
        if (numTickets== null ||numTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets must be greater than 0.");
        }
        if (session == null) {
            throw new IllegalArgumentException("Session must not be null.");
        }

        Event event = eventService.findByName(selectedEvent);
        if (event == null) {
            throw new IllegalArgumentException("Event not found: " + selectedEvent);
        }

        int alreadyBooked = eventService.countTicketsBookedForEvent(event.getId());

        Object discountAttr = session.getAttribute("discount");
        if (!(discountAttr instanceof Double)) {
            throw new IllegalArgumentException("Discount in session must be a valid Double.");
        }

        double loyaltyDiscount = (Double) discountAttr;
        double basePrice = event.calculatePrice(numTickets, alreadyBooked, LocalDateTime.now());
        double totalPrice = basePrice * (1 - loyaltyDiscount / 100);

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
