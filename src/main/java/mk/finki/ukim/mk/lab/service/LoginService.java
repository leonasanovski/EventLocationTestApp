package mk.finki.ukim.mk.lab.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;


public interface LoginService {
    void processLogin(String username, HttpSession session);
     int simulateBookings(String username);
}
