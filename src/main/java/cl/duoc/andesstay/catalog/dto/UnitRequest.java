package cl.duoc.andesstay.catalog.dto;

import cl.duoc.andesstay.catalog.model.UnitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class UnitRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotNull(message = "El tipo es obligatorio (HABITACION, CABANA, LODGE)")
    private UnitType type;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser mayor a 0")
    private Integer capacity;

    @NotNull(message = "El precio por noche es obligatorio")
    @Positive(message = "El precio por noche debe ser mayor a 0")
    private BigDecimal pricePerNight;

    private Boolean available;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UnitType getType() {
        return type;
    }

    public void setType(UnitType type) {
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
