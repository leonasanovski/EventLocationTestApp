package mk.finki.ukim.mk.lab.web;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.BookingCart;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.service.BookingCartService;
import mk.finki.ukim.mk.lab.service.EventBookingService;
import mk.finki.ukim.mk.lab.service.EventService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/eventBooking")
public class EventBookingController {
    private  final EventBookingService eventBookingService;
    private final EventService eventService;
    private final BookingCartService bookingCartService;

    public EventBookingController(EventBookingService eventBookingService, EventService eventService, BookingCartService bookingCartService) {
        this.eventBookingService = eventBookingService;
        this.eventService = eventService;
        this.bookingCartService = bookingCartService;
    }

//    @PostMapping("/events/book_event")
//    public String bookEvent(@RequestParam String username,
//                            @RequestParam String address,
//                            @RequestParam int numTickets,
//                            @RequestParam String selectedEvent,
//                            HttpServletRequest request) {
//
//        Event event = eventService.findByName(selectedEvent);
//        EventBooking booking = eventBookingService.bookEvent(
//                username,
//                address,
//                event.getId(),
//                (long) numTickets
//        );
//
//        request.getSession().setAttribute("username", booking.getAttendeeName());
//        request.getSession().setAttribute("attendeeAddress", booking.getAttendeeAddress());
//        request.getSession().setAttribute("numTickets", booking.getNumberOfTickets());
//        request.getSession().setAttribute("selectedEvent", booking.getEvent().getName());
//        request.getSession().setAttribute("totalPrice", booking.getTotalPrice());
//
//        return "bookingConfirmation";
//    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String selectedEvent,
                            @RequestParam int numTickets,
                            @RequestParam String username,
                            @RequestParam String address,
                            HttpServletRequest request) {
        BookingCart c=bookingCartService.addToCart(selectedEvent, numTickets, username, address, request.getSession());
        request.getSession().setAttribute("cart", c);
        return "redirect:/eventBooking/cart/view";
    }
    @GetMapping("/cart/view")
    public String viewCart(HttpServletRequest request, Model model) {
        if(request.getSession().getAttribute("cart")!=null){
            BookingCart cart = (BookingCart) request.getSession().getAttribute("cart");
            model.addAttribute("cart", cart);
        }else {
            BookingCart cart= new BookingCart();
            model.addAttribute("cart", cart);
        }
        return "cartReview";
    }

    @PostMapping("/cart/confirm")
    public String confirmCart(HttpServletRequest request) {
        BookingCart cart = (BookingCart) request.getSession().getAttribute("cart");
        if (cart != null) {
            for (EventBooking booking : cart.getItems()) {
                eventBookingService.saveBooking(booking);
            }
                       request.getSession().setAttribute("username", cart.getAttendeeName());
            request.getSession().setAttribute("attendeeAddress", cart.getAddress());
            request.getSession().setAttribute("numTickets", cart.getItems().stream().mapToLong(EventBooking::getNumberOfTickets).sum());
            request.getSession().setAttribute("selectedEvent", cart.getItems().size() == 1 ?
                    cart.getItems().get(0).getEvent().getName() : "Multiple Events");
            request.getSession().setAttribute("totalPrice",  cart.getTotalPrice());
            request.getSession().removeAttribute("cart");
        }
        return "bookingConfirmation";
    }

}
