package propertyrental.platform.listing.domain;

public class ListingStatusInUseException extends RuntimeException {

    public ListingStatusInUseException(Long id) {
        super("Listing status with id " + id + " is still used by properties and cannot be deleted");
    }
}
