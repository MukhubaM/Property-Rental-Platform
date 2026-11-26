package propertyrental.platform.location.domain;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(Long id) {
        super("City with id " + id + " was not found");
    }
}
