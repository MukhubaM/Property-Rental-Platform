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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import propertyrental.platform.location.api.dto.request.CreateCityRequest;
import propertyrental.platform.location.api.dto.request.UpdateCityRequest;
import propertyrental.platform.location.api.dto.response.CityResponse;
import propertyrental.platform.location.application.service.CityService;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityResponse> getAllCities(@RequestParam(name = "provinceId", required = false) Long provinceId) {

        return cityService.getAllCities(provinceId);
    }

    @GetMapping("/{id}")
    public CityResponse getCityById(@PathVariable("id") Long id) {

        return cityService.getCityById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CityResponse createCity(@Valid @RequestBody CreateCityRequest request) {

        return cityService.createCity(request);
    }

    @PutMapping("/{id}")
    public CityResponse updateCity(@PathVariable("id")
                                   Long id,
                                   @Valid @RequestBody
                                   UpdateCityRequest request) {

        return cityService.updateCity(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable("id") Long id) {
        cityService.deleteCity(id);
    }
}