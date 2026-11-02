package propertyrental.platform.identity.domain;

public class RoleNotFoundException extends RuntimeException {

    private final Long roleId;

    public RoleNotFoundException(Long roleId) {
        super("Role with id " + roleId + " was not found");
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }
}