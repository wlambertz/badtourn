package dev.wlambertz.rallyon.iam.keycloak.core;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Configuration required to validate Keycloak tokens and translate roles.
 */
public record KeycloakConfig(
        URI issuerUri,
        String audience,
        String userIdClaim,
        Map<String, String> roleMappings,
        String resourceAccessClientId
) {

    private static final Map<String, String> DEFAULT_ROLE_MAPPINGS = Map.of(
            "rallyon-organizer", "ROLE_ORGANIZER",
            "rallyon-participants", "ROLE_PARTICIPANTS",
            "rallyon-audience", "ROLE_AUDIENCE",
            "rallyon-service", "ROLE_SERVICE"
    );
    private static final String DEFAULT_USER_ID_CLAIM = "rallyon_user_id";

    public KeycloakConfig {
        Objects.requireNonNull(issuerUri, "issuerUri");
        Objects.requireNonNull(audience, "audience");
        userIdClaim = userIdClaim == null || userIdClaim.isBlank()
                ? DEFAULT_USER_ID_CLAIM
                : userIdClaim;
        roleMappings = roleMappings == null || roleMappings.isEmpty()
                ? DEFAULT_ROLE_MAPPINGS
                : Map.copyOf(roleMappings);
        resourceAccessClientId = resourceAccessClientId == null || resourceAccessClientId.isBlank()
                ? null
                : resourceAccessClientId;
    }

    public Set<String> configuredRoles() {
        return roleMappings.keySet();
    }
}
