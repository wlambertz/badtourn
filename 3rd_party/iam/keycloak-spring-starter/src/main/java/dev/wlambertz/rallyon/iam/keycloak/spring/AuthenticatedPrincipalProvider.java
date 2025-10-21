package dev.wlambertz.rallyon.iam.keycloak.spring;

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipal;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipalFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.Optional;

/**
 * Convenience access to the current Keycloak principal.
 */
public class AuthenticatedPrincipalProvider {

    private final KeycloakPrincipalFactory principalFactory;

    public AuthenticatedPrincipalProvider(KeycloakPrincipalFactory principalFactory) {
        this.principalFactory = principalFactory;
    }

    public Optional<KeycloakPrincipal> currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken token) {
            Jwt jwt = token.getToken();
            Map<String, Object> claims = jwt.getClaims();
            return Optional.of(principalFactory.fromClaims(claims));
        }
        return Optional.empty();
    }

    public KeycloakPrincipal requirePrincipal() {
        return currentPrincipal()
                .orElseThrow(() -> new IllegalStateException("No Keycloak principal in security context."));
    }
}
