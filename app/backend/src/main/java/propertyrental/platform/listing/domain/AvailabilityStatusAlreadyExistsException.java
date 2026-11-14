package propertyrental.platform.listing.domain;

public class AvailabilityStatusAlreadyExistsException extends RuntimeException {

    public AvailabilityStatusAlreadyExistsException(String field, String value) {
        super("An availability status with " + field + " '" + value + "' already exists");
    }
}
