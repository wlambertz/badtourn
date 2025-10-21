package dev.wlambertz.rallyon.iam.keycloak.core;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KeycloakPrincipalFactoryTest {

    private final KeycloakConfig config = new KeycloakConfig(
            URI.create("http://localhost:8081/realms/rallyon"),
            "rallyon-api",
            "rallyon_user_id",
            Map.of(
                    "rallyon-organizer", "ROLE_ORGANIZER",
                    "rallyon-service", "ROLE_SERVICE"
            ),
            null
    );

    private final KeycloakPrincipalFactory factory = new KeycloakPrincipalFactory(config);

    @Test
    void mapsRealmRoles() {
        KeycloakPrincipal principal = factory.fromClaims(Map.of(
                "sub", "42",
                "rallyon_user_id", 42,
                "realm_access", Map.of("roles", List.of("rallyon-organizer", "irrelevant"))
        ));

        assertThat(principal.subject()).isEqualTo("42");
        assertThat(principal.userId()).hasValue(42);
        assertThat(principal.roles()).containsExactly("ROLE_ORGANIZER");
    }

    @Test
    void rejectsMissingUserId() {
        assertThatThrownBy(() -> factory.fromClaims(Map.of(
                "sub", "abc",
                "realm_access", Map.of("roles", List.of("rallyon-organizer"))
        ))).isInstanceOf(KeycloakValidationException.class)
                .hasMessageContaining("rallyon_user_id");
    }

    @Test
    void rejectsWhenNoMappedRolePresent() {
        assertThatThrownBy(() -> factory.fromClaims(Map.of(
                "sub", "42",
                "rallyon_user_id", 42,
                "realm_access", Map.of("roles", List.of("another-role"))
        ))).isInstanceOf(KeycloakValidationException.class)
                .hasMessageContaining("mapped roles");
    }
}
