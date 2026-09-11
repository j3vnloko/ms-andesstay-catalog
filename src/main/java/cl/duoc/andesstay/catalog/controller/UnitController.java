package cl.duoc.andesstay.catalog.controller;

import cl.duoc.andesstay.catalog.dto.UnitRequest;
import cl.duoc.andesstay.catalog.model.Unit;
import cl.duoc.andesstay.catalog.service.UnitNotFoundException;
import cl.duoc.andesstay.catalog.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public List<Unit> getAll() {
        return unitService.findAll();
    }

    @GetMapping("/{id}")
    public Unit getById(@PathVariable Long id) {
        return unitService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Unit create(@Valid @RequestBody UnitRequest request) {
        return unitService.create(request);
    }

    @PutMapping("/{id}")
    public Unit update(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
        return unitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        unitService.delete(id);
    }

    @ExceptionHandler(UnitNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(UnitNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
