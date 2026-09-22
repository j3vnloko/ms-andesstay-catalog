package cl.duoc.andesstay.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SOLO PARA DESARROLLO LOCAL. Se activa con el perfil "nosec"
 * (spring.profiles.active=nosec), y deja todo abierto para poder probar
 * los endpoints de catalog ANTES de tener el User Pool de Cognito creado.
 *
 * Arrancar con: mvn spring-boot:run -Dspring-boot.run.profiles=nosec
 *
 * NUNCA usar este perfil en la demo/presentacion real ni en produccion -
 * es solo un atajo temporal mientras se arma el resto.
 */
@Configuration
@EnableWebSecurity
@Profile("nosec")
public class SecurityConfigNoAuth {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
