package propertyrental.platform.listing.domain;

public class FacilityNotFoundException extends RuntimeException {

    public FacilityNotFoundException(Long id) {
        super("Facility with id " + id + " was not found");
    }
}