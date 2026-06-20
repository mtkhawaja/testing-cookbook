package com.muneebkhawaja.testing.cookbook.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/// Method-secured service. `@PreAuthorize` is enforced by an AOP proxy (so the class is intentionally
/// not `final`); a caller without `ROLE_ADMIN` gets an `AccessDeniedException`.
@Service
public class AccountService {

    @PreAuthorize("hasRole('ADMIN')")
    public String readAccount(final String accountId) {
        return "account:%s".formatted(accountId);
    }
}
