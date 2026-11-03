package propertyrental.platform.identity.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import propertyrental.platform.identity.api.dto.request.UpdateRoleRequest;
import propertyrental.platform.identity.api.dto.response.RoleResponse;
import propertyrental.platform.identity.application.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<RoleResponse> getAllRoles(){
        return roleService.getAllRoles();
    }

    @GetMapping("/{id}")
    public RoleResponse getRoleById(@PathVariable("id") Long id) {
        return roleService.getRoleById(id);
    }

    @PutMapping("/{id}")
    public RoleResponse updateRole(@PathVariable("id") Long id,
                                   @Valid
                                   @RequestBody
                                   UpdateRoleRequest request
                                    ) {

        return roleService.updateRole(id, request);
    }
}
