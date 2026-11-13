package cl.duoc.andesstay.catalog.controller;

import cl.duoc.andesstay.catalog.config.SecurityConfig;
import cl.duoc.andesstay.catalog.dto.UnitResponse;
import cl.duoc.andesstay.catalog.exception.GlobalExceptionHandler;
import cl.duoc.andesstay.catalog.security.CognitoJwtAuthenticationConverter;
import cl.duoc.andesstay.catalog.security.RestAccessDeniedHandler;
import cl.duoc.andesstay.catalog.security.RestAuthenticationEntryPoint;
import cl.duoc.andesstay.catalog.service.UnitNotFoundException;
import cl.duoc.andesstay.catalog.service.UnitService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UnitController.class)
@Import({SecurityConfig.class, CognitoJwtAuthenticationConverter.class,
        RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class,
        GlobalExceptionHandler.class})
class UnitControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UnitService unitService;

    // --- 401: sin token ---

    @Test
    void getAll_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/catalog/units"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    // --- GET con USER -> 200 ---

    @Test
    void getAll_withUserRole_returns200() throws Exception {
        when(unitService.findAll()).thenReturn(List.of(
                new UnitResponse(1L, "Hab 101", "HABITACION", 2, new BigDecimal("35000"), true)
        ));

        mockMvc.perform(get("/api/catalog/units")
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hab 101"));
    }

    // --- POST con USER -> 403 ---

    @Test
    void create_withUserRole_returns403() throws Exception {
        String body = """
                {"name":"Test","type":"CABANA","capacity":4,"pricePerNight":50000}
                """;

        mockMvc.perform(post("/api/catalog/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    // --- POST con ADMIN y body valido -> 201 ---

    @Test
    void create_withAdminRole_validBody_returns201() throws Exception {
        UnitResponse response = new UnitResponse(1L, "Cabana Lago", "CABANA", 4,
                new BigDecimal("85000"), true);
        when(unitService.create(any())).thenReturn(response);

        String body = """
                {"name":"Cabana Lago","type":"CABANA","capacity":4,"pricePerNight":85000,"available":true}
                """;

        mockMvc.perform(post("/api/catalog/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Cabana Lago"));
    }

    // --- POST con body invalido -> 400 ---

    @Test
    void create_withAdminRole_invalidBody_returns400() throws Exception {
        String body = """
                {"name":"","type":"CABANA"}
                """;

        mockMvc.perform(post("/api/catalog/units")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // --- GET id inexistente -> 404 ---

    @Test
    void getById_notFound_returns404() throws Exception {
        when(unitService.findById(99L)).thenThrow(new UnitNotFoundException(99L));

        mockMvc.perform(get("/api/catalog/units/99")
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No se encontro la unidad con id 99"));
    }

    // --- DELETE con ADMIN -> 204 ---

    @Test
    void delete_withAdminRole_returns204() throws Exception {
        mockMvc.perform(delete("/api/catalog/units/1")
                        .with(jwt().authorities(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    // --- Actuator health: verificamos que NO requiere autenticacion ---
    // Nota: @WebMvcTest no levanta actuator endpoints; solo verificamos que
    // el path /actuator/health no devuelve 401 (la seguridad lo deja pasar).
    // En un test de integracion completo (@SpringBootTest) retornaria 200.
    @Test
    void actuatorHealth_withoutToken_doesNotReturn401() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertNotEquals(401,
                                result.getResponse().getStatus(),
                                "El endpoint /actuator/health no debe requerir autenticacion"));
    }
}
