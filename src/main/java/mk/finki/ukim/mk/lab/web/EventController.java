package mk.finki.ukim.mk.lab.web;

import jakarta.servlet.http.HttpSession;
import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.service.EventService;
import mk.finki.ukim.mk.lab.service.LocationService;
import mk.finki.ukim.mk.lab.service.LoginService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final LocationService locationService;
    private  final LoginService loginService;

    public EventController(EventService eventService, LocationService locationService, LoginService loginService) {
        this.eventService = eventService;
        this.locationService = locationService;
        this.loginService = loginService;
    }

    @GetMapping()
    public String getEventsPage(@RequestParam(required = false) String error, Model model,HttpSession session) {
        List<Event> eventList = this.eventService.listAll();
        Map<Long, Integer> ticketsLeft = eventService.getRemainingTicketsForAllEvents();
        Map<Long, Double> eventPrices = eventService.getDynamicPricesForAllEvents();

        if (session.getAttribute("loginProcessed") == null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            loginService.processLogin(username, session);
            session.setAttribute("loginProcessed", true);
        }

        model.addAttribute("event_list", eventList);
        model.addAttribute("bookedTickets", ticketsLeft);
        model.addAttribute("eventPrices", eventPrices);
        return "listEvents";
    }
    @PostMapping("/filter_events")
    public String filterList(@RequestParam(required = false) String txt,
                             @RequestParam(required = false) Double rating,
                             Model model){
        String t = (txt != null) ? txt.trim() : "";
        Double r = (rating != null) ? rating : 0.0;

        if(!t.isEmpty() && !r.isNaN()){
            List<Event> events_filtered = this.eventService.listAll()
                    .stream()
                    .filter(k -> k.getName().contains(t) && k.getPopularityScore() >= r)
                    .toList();
            model.addAttribute("event_list",events_filtered);
        }else{
            model.addAttribute("event_list", this.eventService.listAll());
        }

        Map<Long, Integer> ticketsLeft = eventService.getRemainingTicketsForAllEvents();
        model.addAttribute("bookedTickets", ticketsLeft);
        Map<Long, Double> eventPrices = eventService.getDynamicPricesForAllEvents();
        model.addAttribute("eventPrices", eventPrices);

        return "listEvents";

    }
    @GetMapping("/add-form")
    @PreAuthorize("hasRole('ADMIN')")
    public String addNewEvent(Model model){
        List<Location> locations = locationService.findAll();
        model.addAttribute("all_locations", locations);
        return "add-event-form";
    }
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteEventFromList(@PathVariable Long id){
        this.eventService.delete(id);
        return "redirect:/events";
    }
    @PostMapping("/add")
    public String saveEvent(@RequestParam(required = false) Long id, // Optional for new events
                            @RequestParam String name,
                            @RequestParam String description,
                            @RequestParam Double popularityScore,
                            @RequestParam Long locationId,
                            @RequestParam LocalDateTime startTime,
                            @RequestParam LocalDateTime endTime,
                            @RequestParam double basePrice,
                            @RequestParam int maxTickets) {

        eventService.save_event(id, name, description, popularityScore, locationId,startTime,endTime,basePrice,maxTickets);
        return "redirect:/events"; // Redirect to the events list
    }

    @PostMapping("/book_event")
    public String BookEvent(@RequestParam String selectedEvent,
                            @RequestParam Integer numTickets,
                            @RequestParam String username, HttpSession session){
        session.setAttribute("username",username);
        session.setAttribute("numTickets",numTickets);
        session.setAttribute("selectedEvent",selectedEvent);
        return "redirect:/eventBooking";
    }
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editEvent(@PathVariable Long id,Model model)
    {
        Event event_to_edit = eventService.findEvent(id).get();
        List<Location> locations = locationService.findAll();
        model.addAttribute("all_locations", locations);
        model.addAttribute("event",event_to_edit);
        return "add-event-form";
    }

}
