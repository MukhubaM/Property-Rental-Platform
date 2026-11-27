package propertyrental.platform.location.domain;

public class SuburbNotFoundException extends RuntimeException {

    public SuburbNotFoundException(Long id) {

        super("Suburb with id " + id + " was not found");
    }
}