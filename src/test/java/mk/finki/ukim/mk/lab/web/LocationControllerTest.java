package mk.finki.ukim.mk.lab.web;

import mk.finki.ukim.mk.lab.model.Location;
import mk.finki.ukim.mk.lab.service.LocationService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    @WithMockUser
    void testLocationsPage() throws Exception {
        Location location = new Location("Arena", "Main Street", "1000", "Big venue");
        Mockito.when(locationService.findAll()).thenReturn(List.of(location));

        mockMvc.perform(get("/locations")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("list"))
                .andExpect(model().attributeExists("location_list"));
    }

    @Test
    @WithMockUser
    void testDeleteLocationFromList() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/locations/delete/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locations"));

        Mockito.verify(locationService).delete(id);
    }

    @Test
    @WithMockUser
    void testAddNewLocation() throws Exception {
        mockMvc.perform(get("/locations/add-loc"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-location-form"));
    }

    @Test
    @WithMockUser
    void testSaveLocation() throws Exception {
        mockMvc.perform(post("/locations/add")
                        .with(csrf())
                        .param("name", "Stadium")
                        .param("address", "Downtown")
                        .param("capacity", "5000")
                        .param("description", "Outdoor space"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/locations"));

        Mockito.verify(locationService)
                .save_location("Stadium", "Downtown", "5000", "Outdoor space");
    }
}
