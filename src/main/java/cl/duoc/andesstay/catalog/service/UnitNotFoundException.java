package cl.duoc.andesstay.catalog.service;

public class UnitNotFoundException extends RuntimeException {

    public UnitNotFoundException(Long id) {
        super("No se encontro la unidad con id " + id);
    }
}
