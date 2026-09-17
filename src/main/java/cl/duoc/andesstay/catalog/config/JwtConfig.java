package cl.duoc.andesstay.catalog.config;

import cl.duoc.andesstay.catalog.security.CognitoAudienceValidator;
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
 * Construye el JwtDecoder del microservicio de catalogo, validando tokens
 * emitidos por un User Pool de AWS Cognito.
 *
 * IMPORTANTE - Cognito tiene dos tipos de token distintos:
 *   - ID token: trae el claim "aud" (el App Client ID), pensado para
 *     identificar al usuario en el propio frontend.
 *   - Access token: NO trae "aud". En su lugar trae el claim "client_id".
 *     Es el que se debe usar como Bearer token para llamar a APIs.
 *
 * Como el frontend debe mandar el ACCESS TOKEN en el header Authorization,
 * este validador revisa "client_id" en vez de "aud" (a diferencia del
 * AudienceValidator que se usaba con Azure AD).
 *
 * issuer-uri esperado (ver application.properties):
 *   https://cognito-idp.{region}.amazonaws.com/{userPoolId}
 * Spring resuelve automaticamente el JWKS en:
 *   {issuer-uri}/.well-known/jwks.json
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
        OAuth2TokenValidator<Jwt> combinedValidator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, withClientId);

        jwtDecoder.setJwtValidator(combinedValidator);
        return jwtDecoder;
    }
}