package propertyrental.platform.listing.domain;

public class AvailabilityStatusInUseException extends RuntimeException {

    public AvailabilityStatusInUseException(Long id) {
        super("Availability status with id " + id + " is still used by rental units and cannot be deleted");
    }
}