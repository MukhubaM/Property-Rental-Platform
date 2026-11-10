package propertyrental.platform.listing.domain;

public class UnitTypeAlreadyExistsException extends RuntimeException {

    public UnitTypeAlreadyExistsException(String field, String value) {
        super("A unit type with " + field + " '" + value + "' already exists");
    }
}