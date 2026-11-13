package cl.duoc.andesstay.catalog.config;

import cl.duoc.andesstay.catalog.security.CognitoAudienceValidator;
import cl.duoc.andesstay.catalog.security.CognitoTokenUseValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Construye el JwtDecoder validando tokens emitidos por un User Pool de AWS Cognito.
 *
 * Validaciones encadenadas en DelegatingOAuth2TokenValidator:
 *   1. Issuer (automatico con JwtValidators.createDefaultWithIssuer)
 *   2. client_id == nuestro App Client ID (CognitoAudienceValidator)
 *   3. token_use == "access" (CognitoTokenUseValidator)
 *   4. Firma (JWKS) y vigencia (exp) son validadas por Nimbus automaticamente.
 */
@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${cognito.app-client-id}")
    private String expectedClientId;

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withClientId = new CognitoAudienceValidator(expectedClientId);
        OAuth2TokenValidator<Jwt> withTokenUse = new CognitoTokenUseValidator();

        OAuth2TokenValidator<Jwt> combinedValidator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, withClientId, withTokenUse);

        jwtDecoder.setJwtValidator(combinedValidator);
        return jwtDecoder;
    }
}
