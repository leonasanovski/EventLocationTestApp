package mk.finki.ukim.mk.lab.service;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.BookingCart;

public interface BookingCartService {
    BookingCart addToCart(String selectedEvent, int numTickets, String username, String address, HttpSession session);
}
