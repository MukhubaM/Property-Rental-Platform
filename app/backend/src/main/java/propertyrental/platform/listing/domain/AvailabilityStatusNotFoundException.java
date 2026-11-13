package propertyrental.platform.listing.domain;

public class AvailabilityStatusNotFoundException extends RuntimeException {

    public AvailabilityStatusNotFoundException(Long id) {
        super("Availability status with id " + id + " was not found");
    }
}
