package dev.wlambertz.rallyon.iam.keycloak.spring;

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakConfig;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipalFactory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakSecurityAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withPropertyValues(
                    "rallyon.security.keycloak.issuer-uri=http://localhost:8081/realms/rallyon",
                    "rallyon.security.keycloak.jwks-uri=http://localhost:8081/realms/rallyon/protocol/openid-connect/certs",
                    "rallyon.security.keycloak.audience=rallyon-api"
            )
            .withConfiguration(AutoConfigurations.of(
                    SecurityAutoConfiguration.class,
                    UserDetailsServiceAutoConfiguration.class,
                    OAuth2ResourceServerAutoConfiguration.class,
                    KeycloakSecurityAutoConfiguration.class
            ));

    @Test
    void registersBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(KeycloakSecurityProperties.class);
            assertThat(context).hasSingleBean(KeycloakConfig.class);
            assertThat(context).hasSingleBean(KeycloakPrincipalFactory.class);
            assertThat(context).hasSingleBean(JwtDecoder.class);
        });
    }
}
