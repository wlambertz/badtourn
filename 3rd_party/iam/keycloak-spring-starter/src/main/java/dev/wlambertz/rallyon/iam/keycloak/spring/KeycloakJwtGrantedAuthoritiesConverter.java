package dev.wlambertz.rallyon.iam.keycloak.spring;

import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipal;
import dev.wlambertz.rallyon.iam.keycloak.core.KeycloakPrincipalFactory;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converts Keycloak JWT roles into Spring authorities.
 */
public final class KeycloakJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final KeycloakPrincipalFactory principalFactory;

    public KeycloakJwtGrantedAuthoritiesConverter(KeycloakPrincipalFactory principalFactory) {
        this.principalFactory = principalFactory;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        KeycloakPrincipal principal = principalFactory.fromClaims(source.getClaims());
        Set<String> authorities = principal.roles();
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableSet());
    }
}
