package mk.finki.ukim.mk.lab.web;

import mk.finki.ukim.mk.lab.model.Event;
import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.service.LocationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping()
    public String getEventsPage(@RequestParam(required = false) String error, Model model){
        List<Location> location_list = this.locationService.findAll();
        model.addAttribute("location_list", location_list);
        return "list";
    }
    @GetMapping("/delete/{id}")
    public String deleteEventFromList(@PathVariable Long id){
        this.locationService.delete(id);
        return "redirect:/locations";
    }
    @GetMapping("/add-loc")
    public String addNewEvent(Model model){
        return "add-location-form";
    }

    @PostMapping("/add")
    public String saveEvent(@RequestParam String name,
                            @RequestParam String address,
                            @RequestParam String capacity,
                            @RequestParam String description) {

        locationService.save_location(name, address, capacity, description);
        return "redirect:/locations"; // Redirect to the events list
    }
}
