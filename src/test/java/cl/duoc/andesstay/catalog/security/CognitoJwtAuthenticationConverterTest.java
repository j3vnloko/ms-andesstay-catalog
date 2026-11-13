package cl.duoc.andesstay.catalog.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CognitoJwtAuthenticationConverterTest {

    private final CognitoJwtAuthenticationConverter converter = new CognitoJwtAuthenticationConverter();

    @Test
    void convert_withGroups_createsRoleAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .claim("cognito:groups", List.of("ADMIN", "USER"))
                .build();

        AbstractAuthenticationToken authToken = converter.convert(jwt);

        assertNotNull(authToken);
        Collection<GrantedAuthority> authorities = authToken.getAuthorities();
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_USER"));
        assertEquals(2, roles.size());
    }

    @Test
    void convert_withoutGroupsClaim_noRoles() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123")
                .claim("token_use", "access")
                .build();

        AbstractAuthenticationToken authToken = converter.convert(jwt);

        assertNotNull(authToken);
        assertTrue(authToken.getAuthorities().isEmpty());
    }

    @Test
    void convert_withSingleGroup_createsSingleRole() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-456")
                .claim("cognito:groups", List.of("admin"))
                .build();

        AbstractAuthenticationToken authToken = converter.convert(jwt);

        List<String> roles = authToken.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertEquals(1, roles.size());
        assertTrue(roles.contains("ROLE_ADMIN")); // toUpperCase
    }
}

