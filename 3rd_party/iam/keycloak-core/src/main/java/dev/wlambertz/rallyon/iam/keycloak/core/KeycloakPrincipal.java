package dev.wlambertz.rallyon.iam.keycloak.core;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;

/**
 * Represents a validated Keycloak principal with mapped authorities.
 */
public final class KeycloakPrincipal {

    private final String subject;
    private final Long userId;
    private final Set<String> roles;
    private final Map<String, Object> claims;

    public KeycloakPrincipal(String subject, Long userId, Set<String> roles, Map<String, Object> claims) {
        this.subject = Objects.requireNonNull(subject, "subject");
        this.userId = userId;
        this.roles = Set.copyOf(Objects.requireNonNull(roles, "roles"));
        this.claims = Collections.unmodifiableMap(Objects.requireNonNull(claims, "claims"));
    }

    public String subject() {
        return subject;
    }

    public OptionalLong userId() {
        return userId == null ? OptionalLong.empty() : OptionalLong.of(userId);
    }

    public Set<String> roles() {
        return roles;
    }

    public Map<String, Object> claims() {
        return claims;
    }
}
