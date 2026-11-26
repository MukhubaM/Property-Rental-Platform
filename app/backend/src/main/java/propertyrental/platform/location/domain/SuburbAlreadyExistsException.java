package propertyrental.platform.location.domain;

public class SuburbAlreadyExistsException extends RuntimeException {

    public SuburbAlreadyExistsException(String name, Long cityId) {

        super("A suburb named '" + name + "' already exists in city " + cityId);
    }
}
