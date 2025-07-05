package mk.finki.ukim.mk.lab.service.implementation;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.bootstrap.LoyaltyUtils;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.repository.jpa.EventBookingRepository;
import mk.finki.ukim.mk.lab.repository.jpa.EventRepository;
import mk.finki.ukim.mk.lab.service.LoginService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LoginServiceImplementation implements LoginService {

    private final EventBookingRepository bookingRepo;
    private final EventRepository eventRepo;


    public LoginServiceImplementation(EventBookingRepository bookingRepo, EventRepository eventRepo) {
        this.bookingRepo = bookingRepo;
        this.eventRepo = eventRepo;
    }

    @Override
    public void processLogin(String username, HttpSession session) {
        // Simulate fake bookings if user is new
        int count = simulateBookings(username);
        // Calculate loyalty status
        String level = LoyaltyUtils.getLoyaltyLevel(count);
        double discount = LoyaltyUtils.getLoyaltyDiscount(count) * 100;
        String message = String.format("Hi %s! You’ve made %d bookings. You're a %s member with %.0f%% discount.",
                username, count, level, discount);
        session.setAttribute("loyaltyMessage", message);
        session.setAttribute("level", level);
        session.setAttribute("discount", discount);
    }
    //MOCK FOR THIS ONE
    //GORJAN
    @Override
    public int simulateBookings(String username) {
        int num = new Random().nextInt(60); // 0–59 bookings
        List<Event> events = eventRepo.findAll();

        for (int i = 0; i < num; i++) {
            Event event = events.get(new Random().nextInt(events.size()));
            bookingRepo.save(new EventBooking(event, username, "Skopje", 1L, event.getBasePrice()));
        }
        return num;
    }
}