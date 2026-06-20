package com.muneebkhawaja.testing.cookbook.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/// HTTP and method security for the module.
///
/// - `/public` is open; `/admin` needs `ROLE_ADMIN`; everything else needs authentication.
/// - HTTP Basic keeps unauthenticated responses a clean `401` (form login would 302 to a login page).
/// - CSRF is on by default, so state-changing requests (`POST`) need a token.
/// - `@EnableMethodSecurity` turns on `@PreAuthorize` for [AccountService].
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // {noop} = no password encoding; for demos/tests only.
        final UserDetails user = User.withUsername("user").password("{noop}password").roles("USER").build();
        final UserDetails admin = User.withUsername("admin").password("{noop}password").roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
