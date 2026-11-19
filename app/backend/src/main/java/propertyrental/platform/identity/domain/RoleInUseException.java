package propertyrental.platform.identity.domain;

public class RoleInUseException extends RuntimeException {

    public RoleInUseException(Long id) {
        super("Role with id " + id + " is still assigned to users and cannot be deleted");
    }
}
