package propertyrental.platform.listing.domain;

public class ListingStatusNotFoundException extends RuntimeException {

    public ListingStatusNotFoundException(Long id) {
        super("Listing status with id " + id + " was not found");
    }
}
