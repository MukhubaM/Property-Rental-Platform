package propertyrental.platform.location.domain;

public class ProvinceInUseException extends RuntimeException {

    public ProvinceInUseException(Long id) {
        super("Province with id " + id + " is still used by cities or properties and cannot be deleted");
    }
}