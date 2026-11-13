package cl.duoc.andesstay.catalog.exception;

import cl.duoc.andesstay.catalog.service.UnitNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejo centralizado de excepciones. Retorna siempre JSON con la clave "error".
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnitNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UnitNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        Map<String, Object> body = new HashMap<>();
        body.put("error", "Error de validacion");
        body.put("details", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(HttpMessageNotReadableException ex) {
        String message = "Cuerpo de la peticion invalido";
        // Extraer mensaje amigable para enum invalido
        if (ex.getMessage() != null && ex.getMessage().contains("UnitType")) {
            message = "Tipo de unidad invalido. Valores permitidos: HABITACION, CABANA, LODGE";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        // No filtrar detalles internos al cliente
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
    }
}

