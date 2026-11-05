package propertyrental.platform.location.domain;

public class ProvinceAlreadyExistsException extends RuntimeException {

    public ProvinceAlreadyExistsException(String field, String value) {
        super("A province with " + field + " '" + value + "' already exists");
    }
}