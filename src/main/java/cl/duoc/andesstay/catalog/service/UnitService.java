package cl.duoc.andesstay.catalog.service;

import cl.duoc.andesstay.catalog.dto.UnitRequest;
import cl.duoc.andesstay.catalog.model.Unit;
import cl.duoc.andesstay.catalog.repository.UnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnitService {

    private final UnitRepository unitRepository;

    public UnitService(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    public Unit findById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new UnitNotFoundException(id));
    }

    public Unit create(UnitRequest request) {
        Unit unit = new Unit(
                request.getName(),
                request.getType(),
                request.getCapacity(),
                request.getPricePerNight(),
                request.getAvailable() != null ? request.getAvailable() : true
        );
        return unitRepository.save(unit);
    }

    public Unit update(Long id, UnitRequest request) {
        Unit existing = findById(id);
        existing.setName(request.getName());
        existing.setType(request.getType());
        existing.setCapacity(request.getCapacity());
        existing.setPricePerNight(request.getPricePerNight());
        if (request.getAvailable() != null) {
            existing.setAvailable(request.getAvailable());
        }
        return unitRepository.save(existing);
    }

    public void delete(Long id) {
        Unit existing = findById(id);
        unitRepository.delete(existing);
    }
}
