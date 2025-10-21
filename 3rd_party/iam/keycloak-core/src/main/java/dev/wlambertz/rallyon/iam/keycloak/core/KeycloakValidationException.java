package dev.wlambertz.rallyon.iam.keycloak.core;

/**
 * Signals malformed or missing token details when parsing a Keycloak JWT.
 */
public class KeycloakValidationException extends RuntimeException {

    public KeycloakValidationException(String message) {
        super(message);
    }

    public KeycloakValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
