package cl.duoc.andesstay.catalog.dto;

import cl.duoc.andesstay.catalog.model.Unit;

import java.math.BigDecimal;

/**
 * DTO de salida para evitar exponer la entidad JPA directamente.
 */
public class UnitResponse {

    private Long id;
    private String name;
    private String type;
    private Integer capacity;
    private BigDecimal pricePerNight;
    private Boolean available;

    public UnitResponse() {
    }

    public UnitResponse(Long id, String name, String type, Integer capacity,
                        BigDecimal pricePerNight, Boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    /**
     * Mapper: convierte una entidad Unit a UnitResponse.
     */
    public static UnitResponse fromEntity(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getName(),
                unit.getType() != null ? unit.getType().name() : null,
                unit.getCapacity(),
                unit.getPricePerNight(),
                unit.getAvailable()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}

