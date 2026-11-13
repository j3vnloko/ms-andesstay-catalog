package cl.duoc.andesstay.catalog.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CognitoAudienceValidatorTest {

    private final CognitoAudienceValidator validator = new CognitoAudienceValidator("my-client-id");

    private Jwt buildJwt(String clientId) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("user-123");
        if (clientId != null) {
            builder.claim("client_id", clientId);
        }
        return builder.build();
    }

    @Test
    void validate_correctClientId_success() {
        Jwt jwt = buildJwt("my-client-id");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertFalse(result.hasErrors());
    }

    @Test
    void validate_incorrectClientId_failure() {
        Jwt jwt = buildJwt("wrong-client-id");
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors());
    }

    @Test
    void validate_missingClientId_failure() {
        Jwt jwt = buildJwt(null);
        OAuth2TokenValidatorResult result = validator.validate(jwt);
        assertTrue(result.hasErrors());
    }
}

