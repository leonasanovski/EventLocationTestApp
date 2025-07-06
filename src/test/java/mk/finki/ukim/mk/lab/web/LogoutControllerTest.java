package mk.finki.ukim.mk.lab.web;

import mk.finki.ukim.mk.lab.config.WebSecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogoutController.class)
@Import(WebSecurityConfiguration.class)

public class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void testLogoutRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}