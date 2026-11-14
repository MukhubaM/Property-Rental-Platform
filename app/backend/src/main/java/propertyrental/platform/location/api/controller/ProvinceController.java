package propertyrental.platform.location.api.controller;

import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import propertyrental.platform.location.api.dto.request.CreateProvinceRequest;
import propertyrental.platform.location.api.dto.request.UpdateProvinceRequest;
import propertyrental.platform.location.api.dto.response.ProvinceResponse;
import propertyrental.platform.location.application.service.ProvinceService;

@RestController
@RequestMapping("/api/v1/provinces")
@RequiredArgsConstructor
@Tag(
        name = "Provinces",
        description = "APIs for managing South African provinces."
)
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    @Operation(
            summary = "Get all provinces",
            description = "Retrieves a list of all provinces available on the platform."
    )
    public List<ProvinceResponse> getAllProvinces() {

        return provinceService.getAllProvinces();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get province by ID",
            description = "Retrieves a specific province using its unique identifier."
    )
    public ProvinceResponse getProvinceById(@PathVariable("id") Long id) {

        return provinceService.getProvinceById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a province",
            description = "Creates a new province using the information provided in the request body."
    )
    public ProvinceResponse createProvince(@Valid @RequestBody CreateProvinceRequest request) {

        return provinceService.createProvince(request);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a province",
            description = "Updates an existing province identified by its ID using the information provided in the request body."
    )
    public ProvinceResponse updateProvince(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateProvinceRequest request
    ) {

        return provinceService.updateProvince(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete a province",
            description = "Deletes an existing province using its unique identifier."
    )
    public void deleteProvince(@PathVariable("id") Long id) {

        provinceService.deleteProvince(id);
    }
}