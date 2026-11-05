package propertyrental.platform.listing.domain;

public class PropertyTypeNotFoundException extends RuntimeException {

    public PropertyTypeNotFoundException(Long id) {
        super("Property type with id " + id + " was not found");
    }
}