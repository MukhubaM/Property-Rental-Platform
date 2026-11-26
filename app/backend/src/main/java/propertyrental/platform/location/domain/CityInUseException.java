package propertyrental.platform.location.domain;

public class CityInUseException extends RuntimeException {

    public CityInUseException(Long id) {
        super("City with id " + id + " is still used by suburbs or properties and cannot be deleted");
    }
}
