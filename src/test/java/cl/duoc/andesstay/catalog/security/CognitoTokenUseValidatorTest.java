package cl.duoc.andesstay.catalog.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CognitoTokenUseValidatorTest {

    private final CognitoTokenUseValidator validator = new CognitoTokenUseValidator();

    private Jwt buildJwt(String tokenUse) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123");
        if (tokenUse != null) {
            builder.claim("token_use", tokenUse);
        }
        return builder.build();
    }

    @Test
    void validate_accessToken_success() {
        Jwt jwt = buildJwt("access");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertFalse(result.hasErrors());
    }

    @Test
    void validate_idToken_failure() {
        Jwt jwt = buildJwt("id");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors());
    }

    @Test
    void validate_missingTokenUse_failure() {
        Jwt jwt = buildJwt(null);
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors());
    }
}

