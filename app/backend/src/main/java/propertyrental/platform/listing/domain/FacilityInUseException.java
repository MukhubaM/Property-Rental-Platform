package propertyrental.platform.listing.domain;

public class FacilityInUseException extends RuntimeException {

    public FacilityInUseException(Long id) {
        super("Facility with id " + id + " is still used by properties or rental units and cannot be deleted");
    }
}
