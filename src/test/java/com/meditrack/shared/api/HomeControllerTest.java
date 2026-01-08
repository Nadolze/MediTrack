package com.meditrack.shared.api;

import com.meditrack.shared.valueobject.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HomeControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController()).build();
    }

    @Test
    void getRoot_withoutSessionUser_shouldShowLandingView() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/landing"));
    }

    @Test
    void getRoot_withSessionUser_shouldShowHomeView() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SessionKeys.LOGGED_IN_USER, new UserSession("1", "john", "john@example.com", "PATIENT"));

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("user/home"))
                .andExpect(model().attributeExists("currentUser"));
    }

    @Test
    void getHome_withoutSessionUser_shouldBehaveLikeRootAndShowLanding() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/landing"));

    }
    @Test
    void getHome_withoutSessionUser_shouldBehaveLikeRootAndShowLanding() throws Exception {
        mockMvc.perform(get("/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/landing"));

    }
}
