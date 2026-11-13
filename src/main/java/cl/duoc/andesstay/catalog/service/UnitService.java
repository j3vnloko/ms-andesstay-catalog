package cl.duoc.andesstay.catalog.service;

import cl.duoc.andesstay.catalog.dto.UnitRequest;
import cl.duoc.andesstay.catalog.dto.UnitResponse;
import cl.duoc.andesstay.catalog.model.Unit;
import cl.duoc.andesstay.catalog.repository.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Transactional(readOnly = true)
    public List<UnitResponse> findAll() {
        return unitRepository.findAll()
                .stream()
                .map(UnitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UnitResponse findById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
        return UnitResponse.fromEntity(unit);
    }

    @Transactional
    public UnitResponse create(UnitRequest request) {
        Unit unit = new Unit(
                request.getName(),
                request.getType(),
                request.getCapacity(),
                request.getPricePerNight(),
                request.getAvailable() != null ? request.getAvailable() : true
        );
        Unit saved = unitRepository.save(unit);
        return UnitResponse.fromEntity(saved);
    }

    @Transactional
    public UnitResponse update(Long id, UnitRequest request) {
        Unit existing = unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
        existing.setName(request.getName());
        existing.setType(request.getType());
        existing.setCapacity(request.getCapacity());
        existing.setPricePerNight(request.getPricePerNight());
        if (request.getAvailable() != null) {
            existing.setAvailable(request.getAvailable());
        }
        Unit saved = unitRepository.save(existing);
        return UnitResponse.fromEntity(saved);
    }

    @Transactional
    public void delete(Long id) {
        Unit existing = unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
        unitRepository.delete(existing);
    }
}
