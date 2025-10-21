package dev.wlambertz.rallyon.iam.keycloak.core;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Converts Keycloak role names into Spring Security authorities.
 */
public final class KeycloakRoleMapper {

    private final Map<String, String> mappings;

    public KeycloakRoleMapper(Map<String, String> mappings) {
        this.mappings = Objects.requireNonNull(mappings, "mappings");
    }

    public Set<String> map(Collection<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        Set<String> granted = new LinkedHashSet<>();
        for (String role : roles) {
            if (role == null || role.isBlank()) {
                continue;
            }
            String normalized = role.trim().toLowerCase(Locale.ROOT);
            String authority = mappings.get(normalized);
            if (authority != null) {
                granted.add(authority);
            }
        }
        return Set.copyOf(granted);
    }
}
