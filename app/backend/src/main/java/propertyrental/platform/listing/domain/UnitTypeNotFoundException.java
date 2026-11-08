package propertyrental.platform.listing.domain;

public class UnitTypeNotFoundException extends RuntimeException {

    public UnitTypeNotFoundException(Long id) {
        super("Unit type with id " + id + " was not found");
    }
}