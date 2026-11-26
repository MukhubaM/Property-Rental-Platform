package propertyrental.platform.location.domain;

public class CityAlreadyExistsException extends RuntimeException {

    public CityAlreadyExistsException(String name, Long provinceId) {
        super("A city named '" + name + "' already exists in province " + provinceId);
    }
}
