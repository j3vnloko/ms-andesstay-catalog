package cl.duoc.andesstay.catalog.service;

import cl.duoc.andesstay.catalog.dto.UnitRequest;
import cl.duoc.andesstay.catalog.dto.UnitResponse;
import cl.duoc.andesstay.catalog.model.Unit;
import cl.duoc.andesstay.catalog.model.UnitType;
import cl.duoc.andesstay.catalog.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    private Unit sampleUnit;

    @BeforeEach
    void setUp() {
        sampleUnit = new Unit("Habitacion 101", UnitType.HABITACION, 2,
                new BigDecimal("35000.00"), true);
        sampleUnit.setId(1L);
    }

    @Test
    void findAll_returnsListOfResponses() {
        when(unitRepository.findAll()).thenReturn(List.of(sampleUnit));

        List<UnitResponse> result = unitService.findAll();

        assertEquals(1, result.size());
        assertEquals("Habitacion 101", result.get(0).getName());
        assertEquals("HABITACION", result.get(0).getType());
        verify(unitRepository).findAll();
    }

    @Test
    void findById_existingId_returnsResponse() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(sampleUnit));

        UnitResponse result = unitService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Habitacion 101", result.getName());
    }

    @Test
    void findById_nonExistingId_throwsUnitNotFoundException() {
        when(unitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UnitNotFoundException.class, () -> unitService.findById(99L));
    }

    @Test
    void create_validRequest_returnsResponse() {
        UnitRequest request = new UnitRequest();
        request.setName("Cabana Lago");
        request.setType(UnitType.CABANA);
        request.setCapacity(4);
        request.setPricePerNight(new BigDecimal("85000"));
        request.setAvailable(true);

        Unit saved = new Unit("Cabana Lago", UnitType.CABANA, 4,
                new BigDecimal("85000"), true);
        saved.setId(2L);

        when(unitRepository.save(any(Unit.class))).thenReturn(saved);

        UnitResponse result = unitService.create(request);

        assertEquals(2L, result.getId());
        assertEquals("Cabana Lago", result.getName());
        assertEquals("CABANA", result.getType());
        verify(unitRepository).save(any(Unit.class));
    }

    @Test
    void update_existingId_returnsUpdatedResponse() {
        UnitRequest request = new UnitRequest();
        request.setName("Habitacion 101 Renovada");
        request.setType(UnitType.HABITACION);
        request.setCapacity(3);
        request.setPricePerNight(new BigDecimal("40000"));
        request.setAvailable(true);

        Unit updated = new Unit("Habitacion 101 Renovada", UnitType.HABITACION, 3,
                new BigDecimal("40000"), true);
        updated.setId(1L);

        when(unitRepository.findById(1L)).thenReturn(Optional.of(sampleUnit));
        when(unitRepository.save(any(Unit.class))).thenReturn(updated);

        UnitResponse result = unitService.update(1L, request);

        assertEquals("Habitacion 101 Renovada", result.getName());
        assertEquals(3, result.getCapacity());
    }

    @Test
    void delete_existingId_deletesSuccessfully() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(sampleUnit));
        doNothing().when(unitRepository).delete(sampleUnit);

        assertDoesNotThrow(() -> unitService.delete(1L));
        verify(unitRepository).delete(sampleUnit);
    }

    @Test
    void delete_nonExistingId_throwsUnitNotFoundException() {
        when(unitRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UnitNotFoundException.class, () -> unitService.delete(99L));
    }
}

