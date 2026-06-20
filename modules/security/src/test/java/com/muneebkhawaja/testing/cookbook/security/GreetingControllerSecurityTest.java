package com.muneebkhawaja.testing.cookbook.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/// URL-level security via `@WebMvcTest` + the imported [WebSecurityConfig].
///
/// MockMvc is built with `apply(springSecurity())` — required so `@WithMockUser` (which populates the
/// thread-local security context) bridges into the filter chain. Identity is then supplied per test
/// via `@WithMockUser`, the `user(...)` post-processor, or real `httpBasic(...)` credentials against
/// the in-memory users.
@WebMvcTest(GreetingController.class)
@Import(WebSecurityConfig.class)
class GreetingControllerSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @DisplayName("Should allow anonymous access When the endpoint is public")
    @Test
    void shouldAllowAnonymousAccessWhenTheEndpointIsPublic() throws Exception {
        mvc.perform(get("/public")).andExpect(status().isOk());
    }

    @DisplayName("Should reject anonymous access When the endpoint requires authentication")
    @Test
    void shouldRejectAnonymousAccessWhenTheEndpointRequiresAuthentication() throws Exception {
        mvc.perform(get("/private")).andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "alice")
    @DisplayName("Should allow access and expose the principal When the user is authenticated")
    @Test
    void shouldAllowAccessAndExposeThePrincipalWhenTheUserIsAuthenticated() throws Exception {
        mvc.perform(get("/private"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello alice"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("Should forbid access When the user lacks the required role")
    @Test
    void shouldForbidAccessWhenTheUserLacksTheRequiredRole() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should allow access When the user has the required role")
    @Test
    void shouldAllowAccessWhenTheUserHasTheRequiredRole() throws Exception {
        mvc.perform(get("/admin")).andExpect(status().isOk());
    }

    @DisplayName("Should reject the POST When the CSRF token is missing")
    @Test
    void shouldRejectThePostWhenTheCsrfTokenIsMissing() throws Exception {
        mvc.perform(post("/messages").with(user("alice"))).andExpect(status().isForbidden());
    }

    @DisplayName("Should accept the POST When a CSRF token and user are supplied")
    @Test
    void shouldAcceptThePostWhenACsrfTokenAndUserAreSupplied() throws Exception {
        mvc.perform(post("/messages").with(user("alice")).with(csrf())).andExpect(status().isOk());
    }

    @DisplayName("Should authenticate When valid HTTP Basic credentials are supplied")
    @Test
    void shouldAuthenticateWhenValidHttpBasicCredentialsAreSupplied() throws Exception {
        mvc.perform(get("/private").with(httpBasic("user", "password")))
                .andExpect(status().isOk())
                .andExpect(content().string("hello user"));
    }
}
