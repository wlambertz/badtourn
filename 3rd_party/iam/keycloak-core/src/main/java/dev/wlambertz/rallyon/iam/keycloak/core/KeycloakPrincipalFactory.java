package dev.wlambertz.rallyon.iam.keycloak.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Builds {@link KeycloakPrincipal} instances from raw JWT claims.
 */
public final class KeycloakPrincipalFactory {

    private static final String REALM_ACCESS = "realm_access";
    private static final String REALM_ROLES = "roles";
    private static final String RESOURCE_ACCESS = "resource_access";

    private final KeycloakConfig config;
    private final KeycloakRoleMapper roleMapper;

    public KeycloakPrincipalFactory(KeycloakConfig config) {
        this.config = Objects.requireNonNull(config, "config");
        this.roleMapper = new KeycloakRoleMapper(config.roleMappings());
    }

    public KeycloakPrincipal fromClaims(Map<String, Object> claims) {
        Objects.requireNonNull(claims, "claims");

        String subject = stringClaim(claims, "sub");
        Map<String, Object> realmAccess = nestedMap(claims, REALM_ACCESS);
        List<String> roles = new ArrayList<>(stringList(realmAccess, REALM_ROLES));

        if (config.resourceAccessClientId() != null) {
            Map<String, Object> resourceAccess = nestedMap(claims, RESOURCE_ACCESS);
            Map<String, Object> client = nestedMap(resourceAccess, config.resourceAccessClientId());
            roles.addAll(stringList(client, REALM_ROLES));
        }

        Set<String> authorities = roleMapper.map(roles);
        if (authorities.isEmpty()) {
            throw new KeycloakValidationException("Token does not contain any mapped roles.");
        }

        Long userId = resolveUserId(claims);

        return new KeycloakPrincipal(subject, userId, authorities, Map.copyOf(claims));
    }

    private Long resolveUserId(Map<String, Object> claims) {
        Object raw = claims.get(config.userIdClaim());
        if (raw == null) {
            throw new KeycloakValidationException(
                    "Token missing required user id claim: " + config.userIdClaim());
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        if (raw instanceof String string) {
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException ex) {
                throw new KeycloakValidationException(
                        "User id claim '" + config.userIdClaim() + "' must be numeric.", ex);
            }
        }
        throw new KeycloakValidationException(
                "Unsupported user id claim type for '" + config.userIdClaim() + "': " + raw.getClass());
    }

    private static Map<String, Object> nestedMap(Map<String, Object> claims, String key) {
        Object raw = claims.getOrDefault(key, Collections.emptyMap());
        if (raw instanceof Map<?, ?> map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cast = (Map<String, Object>) map;
            return cast;
        }
        return Collections.emptyMap();
    }

    private static List<String> stringList(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (raw instanceof Collection<?> collection) {
            List<String> values = new ArrayList<>();
            for (Object entry : collection) {
                if (entry instanceof String str) {
                    values.add(str);
                }
            }
            return values;
        }
        return List.of();
    }

    private static String stringClaim(Map<String, Object> claims, String key) {
        Object value = claims.get(key);
        if (value instanceof String str && !str.isBlank()) {
            return str;
        }
        throw new KeycloakValidationException("Token missing required claim: " + key);
    }
}
