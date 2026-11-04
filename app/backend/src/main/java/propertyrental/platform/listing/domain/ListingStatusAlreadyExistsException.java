package propertyrental.platform.listing.domain;

public class ListingStatusAlreadyExistsException extends RuntimeException {

    public ListingStatusAlreadyExistsException(String field, String value) {
        super("A listing status with " + field + " '" + value + "' already exists");
    }
}