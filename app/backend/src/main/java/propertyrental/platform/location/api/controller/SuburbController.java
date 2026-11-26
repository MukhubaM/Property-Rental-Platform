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

import propertyrental.platform.location.api.dto.request.CreateSuburbRequest;
import propertyrental.platform.location.api.dto.request.UpdateSuburbRequest;
import propertyrental.platform.location.api.dto.response.SuburbResponse;
import propertyrental.platform.location.application.service.SuburbService;

@RestController
@RequestMapping("/api/v1/suburbs")
@RequiredArgsConstructor
public class SuburbController {

    private final SuburbService suburbService;

    @GetMapping
    public List<SuburbResponse> getAllSuburbs(@RequestParam(name = "cityId", required = false) Long cityId) {

        return suburbService.getAllSuburbs(cityId);
    }

    @GetMapping("/{id}")
    public SuburbResponse getSuburbById(@PathVariable("id") Long id) {

        return suburbService.getSuburbById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SuburbResponse createSuburb(@Valid @RequestBody CreateSuburbRequest request) {

        return suburbService.createSuburb(request);
    }

    @PutMapping("/{id}")
    public SuburbResponse updateSuburb(@PathVariable("id")
                                       Long id,
                                       @Valid @RequestBody
                                       UpdateSuburbRequest request) {

        return suburbService.updateSuburb(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSuburb(@PathVariable("id") Long id) {
        suburbService.deleteSuburb(id);
    }
}
