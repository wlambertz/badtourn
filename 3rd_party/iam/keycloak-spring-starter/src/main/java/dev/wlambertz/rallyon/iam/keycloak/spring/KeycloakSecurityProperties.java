package dev.wlambertz.rallyon.iam.keycloak.spring;

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "rallyon.security.keycloak")
public class KeycloakSecurityProperties {

    private boolean enabled = true;
    private URI issuerUri;
    private URI jwksUri;
    private String audience = "rallyon-api";
    private String userIdClaim = "rallyon_user_id";
    private Map<String, String> roleMappings = new LinkedHashMap<>();
    private String resourceAccessClientId;
    private List<String> permitAll = List.of("/actuator/health/**", "/actuator/info");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public URI getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(URI issuerUri) {
        this.issuerUri = issuerUri;
    }

    public URI getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(URI jwksUri) {
        this.jwksUri = jwksUri;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getUserIdClaim() {
        return userIdClaim;
    }

    public void setUserIdClaim(String userIdClaim) {
        this.userIdClaim = userIdClaim;
    }

    public Map<String, String> getRoleMappings() {
        return roleMappings;
    }

    public void setRoleMappings(Map<String, String> roleMappings) {
        this.roleMappings = roleMappings == null ? new LinkedHashMap<>() : new LinkedHashMap<>(roleMappings);
    }

    public String getResourceAccessClientId() {
        return resourceAccessClientId;
    }

    public void setResourceAccessClientId(String resourceAccessClientId) {
        this.resourceAccessClientId = resourceAccessClientId;
    }

    public List<String> getPermitAll() {
        return permitAll;
    }

    public void setPermitAll(List<String> permitAll) {
        this.permitAll = permitAll == null ? List.of() : List.copyOf(permitAll);
    }

    public KeycloakConfig toConfig() {
        return new KeycloakConfig(
                issuerUri,
                audience,
                userIdClaim,
                roleMappings,
                resourceAccessClientId
        );
    }
}
