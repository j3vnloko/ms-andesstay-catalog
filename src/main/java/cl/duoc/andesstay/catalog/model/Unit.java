package cl.duoc.andesstay.catalog.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "units")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UnitType type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "price_per_night", nullable = false)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private Boolean available = true;

    public Unit() {
    }

    public Unit(String name, UnitType type, Integer capacity, BigDecimal pricePerNight, Boolean available) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
        this.available = available;
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
