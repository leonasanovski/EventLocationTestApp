package mk.finki.ukim.mk.lab.web;

import mk.finki.ukim.mk.lab.model.BookingCart;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.EventBooking;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.service.BookingCartService;
import mk.finki.ukim.mk.lab.service.EventBookingService;
import mk.finki.ukim.mk.lab.service.EventService;
import org.apache.catalina.security.SecurityConfig;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@WebMvcTest(controllers = EventBookingController.class)
@Import(SecurityConfig.class)
public class EventBookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private BookingCartService bookingCartService;

    @MockBean
    private EventBookingService eventBookingService;

    @Test
    public void testCartConfirm() throws Exception {
        EventBooking booking = new EventBooking();
        booking.setNumberOfTickets(2L);
        booking.setTotalPrice(100.0);

        Event event = new Event("Concert", "Live show", 4.5,
                new Location("Stadium", "City Center", "1000", "Outdoor"),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                50.0, 200);
        booking.setEvent(event);

        BookingCart cart = new BookingCart();
        cart.setAttendeeName("John Doe");
        cart.setAddress("Test Street");
        cart.setItems(List.of(booking));

        Mockito.doNothing().when(eventBookingService).saveBooking(any(EventBooking.class));

        mockMvc.perform(post("/eventBooking/cart/confirm")
                        .with(csrf())
                        .with(user("testuser").roles("USER"))
                        .sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(view().name("bookingConfirmation"))
                .andExpect(request().sessionAttribute("username", "John Doe"))
                .andExpect(request().sessionAttribute("attendeeAddress", "Test Street"))
                .andExpect(request().sessionAttribute("numTickets", 2L))
                .andExpect(request().sessionAttribute("selectedEvent", "Concert"))
                .andExpect(request().sessionAttribute("totalPrice", 100.0))
                .andExpect(request().sessionAttributeDoesNotExist("cart"));
    }
    @Test
    public void testAddToCart() throws Exception {
        String selectedEvent = "Concert";
        int numTickets = 2;
        String username = "Jane Doe";
        String address = "123 Main St";

        // Mocked event returned from eventService.findByName
        Event event = new Event("Concert", "Live show", 4.5,
                new Location("Arena", "Center", "5000", "Big Hall"),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                50.0, 200);

        // Booking returned from eventBookingService
        EventBooking booking = new EventBooking(event, username, address, (long) numTickets,0);
        booking.setTotalPrice(100.0);

        Mockito.when(eventBookingService.bookEvent(username, address, event.getId(), (long) numTickets))
                .thenReturn(booking);
        Mockito.when(eventService.findByName(selectedEvent)).thenReturn(event);

        mockMvc.perform(post("/eventBooking/cart/add")
                        .with(csrf())
                        .with(user("testuser").roles("USER"))
                        .param("selectedEvent", selectedEvent)
                        .param("numTickets", String.valueOf(numTickets))
                        .param("username", username)
                        .param("address", address))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventBooking/cart/view"))
                ;
    }
    @Test
    public void testCartView_withExistingCart() throws Exception {
        Event event = new Event("Concert", "Live show", 4.5,
                new Location("Arena", "Center", "5000", "Big Hall"),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                50.0, 200);

        EventBooking booking = new EventBooking(event, "Jane Doe", "123 Main St", (long) 2,0);
        booking.setTotalPrice(100.0);


        BookingCart cart = new BookingCart();
        cart.setAddress("123 Main St");
        cart.setItems(List.of(booking));
        cart.setAttendeeName("John Doe");

        mockMvc.perform(get("/eventBooking/cart/view")
                        .with(user("testuser").roles("USER"))
                        .sessionAttr("cart", cart))
                .andExpect(status().isOk())
                .andExpect(view().name("cartReview"))
                .andExpect(model().attributeExists("cart"));
    }
    @Test
    public void testCartView_withoutExistingCart() throws Exception {
        mockMvc.perform(get("/eventBooking/cart/view")
                        .with(user("testuser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("cartReview"))
                .andExpect(model().attributeExists("cart"));
    }
}