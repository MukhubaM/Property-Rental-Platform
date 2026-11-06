package propertyrental.platform.listing.domain;

public class PropertyTypeAlreadyExistsException extends RuntimeException {

    public PropertyTypeAlreadyExistsException(String field, String value) {
        super("A property type with " + field + " '" + value + "' already exists");
    }
}