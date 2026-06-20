package com.muneebkhawaja.testing.cookbook.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Method security (`@PreAuthorize`) via a full context. The security context is populated by
/// `@WithMockUser`; an unauthorized role causes the proxied call to throw `AccessDeniedException`.
@SpringBootTest(classes = SecurityTestApplication.class)
class AccountServiceMethodSecurityTest {

    @Autowired
    private AccountService accountService;

    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should read the account When the user has the ADMIN role")
    @Test
    void shouldReadTheAccountWhenTheUserHasTheAdminRole() {
        assertThat(accountService.readAccount("12345678")).isEqualTo("account:12345678");
    }

    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access When the user lacks the ADMIN role")
    @Test
    void shouldDenyAccessWhenTheUserLacksTheAdminRole() {
        assertThatThrownBy(() -> accountService.readAccount("12345678"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @WithAnonymousUser
    @DisplayName("Should deny access When the user is anonymous")
    @Test
    void shouldDenyAccessWhenTheUserIsAnonymous() {
        assertThatThrownBy(() -> accountService.readAccount("12345678"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
