package propertyrental.platform.listing.domain;

public class PropertyTypeInUseException extends RuntimeException {

    public PropertyTypeInUseException(Long id) {
        super("Property type with id " + id + " is still used by properties and cannot be deleted");
    }
}