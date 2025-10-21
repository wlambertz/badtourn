package dev.wlambertz.rallyon.iam.keycloak.spring;

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakConfig;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipalFactory;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.net.URI;

@AutoConfiguration
@ConditionalOnClass({JwtDecoder.class, HttpSecurity.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(KeycloakSecurityProperties.class)
@ConditionalOnProperty(prefix = "rallyon.security.keycloak", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KeycloakSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public KeycloakConfig keycloakConfig(KeycloakSecurityProperties properties) {
        return properties.toConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public KeycloakPrincipalFactory keycloakPrincipalFactory(KeycloakConfig config) {
        return new KeycloakPrincipalFactory(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticatedPrincipalProvider authenticatedPrincipalProvider(KeycloakPrincipalFactory factory) {
        return new AuthenticatedPrincipalProvider(factory);
    }

    @Bean
    @ConditionalOnMissingBean(name = "keycloakJwtAuthenticationConverter")
    public Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtAuthenticationConverter(
            KeycloakPrincipalFactory factory
    ) {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("preferred_username");
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtGrantedAuthoritiesConverter(factory));
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtDecoder keycloakJwtDecoder(KeycloakSecurityProperties properties) {
        URI issuer = properties.getIssuerUri();
        if (issuer == null) {
            throw new IllegalStateException("rallyon.security.keycloak.issuer-uri must be provided.");
        }

        NimbusJwtDecoder decoder = properties.getJwksUri() != null
                ? NimbusJwtDecoder.withJwkSetUri(properties.getJwksUri().toString()).build()
                : NimbusJwtDecoder.withIssuerLocation(issuer.toString()).build();

        OAuth2TokenValidator<Jwt> validator = token -> OAuth2TokenValidatorResult.success();
        decoder.setJwtValidator(token -> {
            OAuth2TokenValidatorResult issuerResult = JwtValidators.createDefaultWithIssuer(issuer.toString()).validate(token);
            if (issuerResult.hasErrors()) {
                return issuerResult;
            }
            String requiredAudience = properties.getAudience();
            if (requiredAudience != null && !requiredAudience.isBlank() && !token.getAudience().contains(requiredAudience)) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Token audience does not match required audience.", null)
                );
            }
            return validator.validate(token);
        });
        return decoder;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityFilterChain keycloakSecurityFilterChain(
            HttpSecurity http,
            KeycloakSecurityProperties properties,
            Converter<Jwt, ? extends AbstractAuthenticationToken> keycloakJwtAuthenticationConverter
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> {
                    for (String pattern : properties.getPermitAll()) {
                        authorize.requestMatchers(pattern).permitAll();
                    }
                    authorize.anyRequest().authenticated();
                })
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)));
        return http.build();
    }
}
