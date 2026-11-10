package propertyrental.platform.listing.domain;


public class UnitTypeInUseException extends RuntimeException {

    public UnitTypeInUseException(Long id) {
        super("Unit type with id " + id + " is still used by rental units and cannot be deleted");
    }
}