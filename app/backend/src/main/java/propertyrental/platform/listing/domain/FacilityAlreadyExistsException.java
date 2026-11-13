package propertyrental.platform.listing.domain;

public class FacilityAlreadyExistsException extends RuntimeException {

    public FacilityAlreadyExistsException(String field, String value) {
        super("A facility with " + field + " '" + value + "' already exists");
    }
}
