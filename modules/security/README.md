# Spring Security Tests

Common Spring Security testing scenarios (Spring Security 7 / Spring Boot 4).

`WebSecurityConfig` secures a small app: `/public` is open, `/admin` needs `ROLE_ADMIN`, everything
else needs authentication (HTTP Basic), CSRF is on, and `@EnableMethodSecurity` guards
`AccountService.readAccount` with `@PreAuthorize`.

## What the tests cover

### `GreetingControllerSecurityTest` — URL/filter-chain security (`@WebMvcTest`)

- anonymous access: allowed on `/public`, `401` on protected endpoints
- `@WithMockUser`: authenticated access, principal exposure, role checks (`USER` → `403`, `ADMIN` → `200`)
- CSRF: `POST` without a token → `403`; with `csrf()` → `200`
- real HTTP Basic via `httpBasic("user", "password")` against the in-memory users

> Gotcha: build MockMvc with `apply(springSecurity())`. Without it, `@WithMockUser` (which sets the
> thread-local context) does not bridge into the filter chain — you'd get `401` even though
> `user(...)`/`httpBasic(...)` post-processors still work.

### `AccountServiceMethodSecurityTest` — method security (`@SpringBootTest`)

- `@WithMockUser(roles = "ADMIN")` → invokes
- `@WithMockUser(roles = "USER")` and `@WithAnonymousUser` → `AccessDeniedException`

### `ArchitectureTest` — ArchUnit fitness rules

`@RestController`s are named `*Controller`, no field injection, no `System.out`, no generic exceptions.
The field-injection rule uses `allowEmptyShould(true)` because these classes are stateless.

## Run

```bash
./mvnw test -pl modules/security
```
