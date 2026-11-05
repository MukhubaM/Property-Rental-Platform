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

import propertyrental.platform.location.api.dto.request.CreateProvinceRequest;
import propertyrental.platform.location.api.dto.request.UpdateProvinceRequest;
import propertyrental.platform.location.api.dto.response.ProvinceResponse;
import propertyrental.platform.location.application.service.ProvinceService;

@RestController
@RequestMapping("/api/v1/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    public List<ProvinceResponse> getAllProvinces() {

        return provinceService.getAllProvinces();
    }

    @GetMapping("/{id}")
    public ProvinceResponse getProvinceById(@PathVariable("id") Long id) {

        return provinceService.getProvinceById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProvinceResponse createProvince(@Valid @RequestBody CreateProvinceRequest request) {

        return provinceService.createProvince(request);
    }

    @PutMapping("/{id}")
    public ProvinceResponse updateProvince(@PathVariable("id")
                                           Long id,
                                           @Valid @RequestBody
                                           UpdateProvinceRequest request) {

        return provinceService.updateProvince(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProvince(@PathVariable("id") Long id) {
        provinceService.deleteProvince(id);
    }
}