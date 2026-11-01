package propertyrental.platform.identity.domain;

public class RoleNotFoundException extends RuntimeException {

    private final Short roleId;

    public RoleNotFoundException(Short roleId) {
        super("Role with id " + roleId + " was not found");
        this.roleId = roleId;
    }

    public Short getRoleId() {

        return roleId;
    }
}


