package mk.finki.ukim.mk.lab.web;


import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.LocationService;
import mk.finki.ukim.mk.lab.service.LoginService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private LocationService locationService;

    @MockBean
    private LoginService loginService;

    @Test
    @WithMockUser
    void testGetEventsPage() throws Exception {
        Event event = new Event("Concert", "Live show", 4.5,
                new Location("Stadium", "City Center", "1000", "Outdoor"),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                50.0, 200);
        event.setId(1L);

        List<Event> eventList = List.of(event);
        Map<Long, Integer> ticketsMap = Map.of(1L, 200);
        Map<Long, Double> priceMap = Map.of(1L, 50.0);

        Mockito.when(eventService.listAll()).thenReturn(eventList);
        Mockito.when(eventService.getRemainingTicketsForAllEvents()).thenReturn(ticketsMap);
        Mockito.when(eventService.getDynamicPricesForAllEvents()).thenReturn(priceMap);

        mockMvc.perform(get("/events")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("listEvents"))
                .andExpect(model().attribute("event_list", eventList))
                .andExpect(model().attribute("bookedTickets", ticketsMap))
                .andExpect(model().attribute("eventPrices", priceMap));
    }

    @Test
    @WithMockUser
    public void testFilterList() throws Exception {
        Event event = new Event("Concert", "Live show", 4.5,
                new Location("Stadium", "City Center", "1000", "Outdoor"),
                LocalDateTime.now(), LocalDateTime.now().plusHours(2),
                50.0, 200);
        event.setId(1L);

        List<Event> eventList = List.of(event);
        Map<Long, Integer> ticketsMap = Map.of(1L, 200);
        Map<Long, Double> priceMap = Map.of(1L, 50.0);

        Mockito.when(eventService.listAll()).thenReturn(eventList);
        Mockito.when(eventService.getRemainingTicketsForAllEvents()).thenReturn(ticketsMap);
        Mockito.when(eventService.getDynamicPricesForAllEvents()).thenReturn(priceMap);

        mockMvc.perform(post("/events/filter_events")
                        .with(csrf())
                        .param("txt", "")
                        .param("rating", "0.0"))
                .andExpect(status().isOk())
                .andExpect(view().name("listEvents"))
                .andExpect(model().attribute("event_list", eventList))
                .andExpect(model().attribute("bookedTickets", ticketsMap))
                .andExpect(model().attribute("eventPrices", priceMap));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddNewEvent() throws Exception {
        Mockito.when(locationService.findAll()).thenReturn(List.of(new Location()));

        mockMvc.perform(get("/events/add-form"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-event-form"))
                .andExpect(model().attributeExists("all_locations"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteEventFromList() throws Exception {
        mockMvc.perform(get("/events/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }

    @Test
    @WithMockUser
    public void testSaveEvent() throws Exception {
        mockMvc.perform(post("/events/add")
                        .with(csrf())
                        .param("name", "Concert")
                        .param("description", "Live Show")
                        .param("popularityScore", "4.5")
                        .param("locationId", "1")
                        .param("startTime", LocalDateTime.now().toString())
                        .param("endTime", LocalDateTime.now().plusHours(2).toString())
                        .param("basePrice", "50.0")
                        .param("maxTickets", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/events"));
    }

    @Test
    @WithMockUser
    public void testBookEvent() throws Exception {
        mockMvc.perform(post("/events/book_event")
                        .with(csrf())
                        .param("selectedEvent", "Concert")
                        .param("numTickets", "2")
                        .param("username", "John Doe"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/eventBooking"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testEditEvent() throws Exception {
        Event event = new Event();
        event.setName("Concert");
        Mockito.when(eventService.findEvent(1L)).thenReturn(Optional.of(event));
        Mockito.when(locationService.findAll()).thenReturn(List.of(new Location()));

        mockMvc.perform(get("/events/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-event-form"))
                .andExpect(model().attributeExists("event"))
                .andExpect(model().attributeExists("all_locations"));
    }
}