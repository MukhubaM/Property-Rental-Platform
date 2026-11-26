package propertyrental.platform.location.domain;

public class SuburbInUseException extends RuntimeException {

    public SuburbInUseException(Long id) {

        super("Suburb with id " + id + " is still used by properties and cannot be deleted");
    }
}