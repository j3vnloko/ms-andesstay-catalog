package cl.duoc.andesstay.catalog.controller;

import cl.duoc.andesstay.catalog.dto.UnitRequest;
import cl.duoc.andesstay.catalog.dto.UnitResponse;
import cl.duoc.andesstay.catalog.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping
    public List<UnitResponse> getAll() {
        return unitService.findAll();
    }

    @GetMapping("/{id}")
    public UnitResponse getById(@PathVariable Long id) {
        return unitService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnitResponse create(@Valid @RequestBody UnitRequest request) {
        return unitService.create(request);
    }

    @PutMapping("/{id}")
    public UnitResponse update(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
        return unitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        unitService.delete(id);
    }
}
