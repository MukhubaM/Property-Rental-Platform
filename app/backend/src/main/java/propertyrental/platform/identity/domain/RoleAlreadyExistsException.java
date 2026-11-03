package propertyrental.platform.identity.domain;

public class RoleAlreadyExistsException extends RuntimeException {

    public RoleAlreadyExistsException(String field, String value) {
        super("A role with " + field + " " + value + " already exists");
    }
}
