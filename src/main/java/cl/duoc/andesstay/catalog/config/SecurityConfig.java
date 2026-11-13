package cl.duoc.andesstay.catalog.config;

import cl.duoc.andesstay.catalog.security.CognitoJwtAuthenticationConverter;
import cl.duoc.andesstay.catalog.security.RestAccessDeniedHandler;
import cl.duoc.andesstay.catalog.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Seguridad del microservicio de catalogo.
 *
 * GET (lectura)           -> cualquier usuario autenticado (ADMIN o USER)
 * POST / PUT / DELETE     -> solo ADMIN
 * /actuator/health        -> publico (health check en EC2)
 *
 * CORS es configurable via propiedades cors.enabled y cors.allowed-origins.
 * Si el API Gateway ya inyecta headers CORS, setear cors.enabled=false para
 * no duplicar headers.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${cors.enabled:true}")
    private boolean corsEnabled;

    private final CognitoJwtAuthenticationConverter cognitoJwtAuthenticationConverter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(CognitoJwtAuthenticationConverter cognitoJwtAuthenticationConverter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler) {
        this.cognitoJwtAuthenticationConverter = cognitoJwtAuthenticationConverter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (corsEnabled) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        } else {
            http.cors(cors -> cors.disable());
        }

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/catalog/units", "/api/catalog/units/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/catalog/units", "/api/catalog/units/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/units/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/units/{id}").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(cognitoJwtAuthenticationConverter)))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
