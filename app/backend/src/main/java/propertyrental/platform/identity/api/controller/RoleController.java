
package propertyrental.platform.identity.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import propertyrental.platform.identity.api.dto.request.UpdateRoleRequest;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(
        name = "Roles",
        description = "APIs for managing platform roles and their associated information."
)
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all roles available on the platform, including their identifiers, codes, and names."
    )
    public List<RoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a specific platform role using its unique identifier."
    )
    public RoleResponse getRoleById(@PathVariable("id") Long id) {
        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a role",
            description = "Updates the information of an existing platform role identified by its ID using the details provided in the request body."
    )
    public RoleResponse updateRole(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        return roleService.updateRole(id, request);
    }
}
