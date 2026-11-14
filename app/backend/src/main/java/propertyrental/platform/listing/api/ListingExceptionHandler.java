package propertyrental.platform.listing.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import propertyrental.platform.listing.domain.ListingStatusAlreadyExistsException;
import propertyrental.platform.listing.domain.ListingStatusInUseException;
import propertyrental.platform.listing.domain.ListingStatusNotFoundException;
import propertyrental.platform.listing.domain.PropertyTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.PropertyTypeNotFoundException;
import propertyrental.platform.listing.domain.PropertyTypeInUseException;
import propertyrental.platform.listing.domain.FacilityAlreadyExistsException;
import propertyrental.platform.listing.domain.FacilityInUseException;
import propertyrental.platform.listing.domain.FacilityNotFoundException;
import propertyrental.platform.listing.domain.AvailabilityStatusNotFoundException;
import propertyrental.platform.listing.domain.UnitTypeAlreadyExistsException;
import propertyrental.platform.listing.domain.UnitTypeInUseException;
import propertyrental.platform.listing.domain.UnitTypeNotFoundException;

@RestControllerAdvice(basePackageClasses = ListingExceptionHandler.class)
public class ListingExceptionHandler {

    // 404 (the requested listing status id doesn't exist)
    @ExceptionHandler(ListingStatusNotFoundException.class)
    public ProblemDetail handleNotFound(ListingStatusNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Listing status not found");

        return problem;
    }

    // 409 (another listing status already uses this code or name)
    @ExceptionHandler(ListingStatusAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(ListingStatusAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Listing status already exists");

        return problem;
    }

    // 409 (if the listing status is still used by properties, so it can't be deleted)
    @ExceptionHandler(ListingStatusInUseException.class)
    public ProblemDetail handleInUse(ListingStatusInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Listing status in use");

        return problem;
    }

    // 409 (fallback when the database unique constraint catches a duplicate the service check missed)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,  "A record with this code or name already exists");
        problem.setTitle("Duplicate record");

        return problem;
    }

    // 400 (if the request body failed validation (blank or too long fields))
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Request validation failed");
        problem.setTitle("Invalid request");
        problem.setProperty("errors", errors);

        return problem;
    }

    // 409 (if another property type already uses this code or name)
    @ExceptionHandler(PropertyTypeAlreadyExistsException.class)
    public ProblemDetail handlePropertyTypeAlreadyExists(PropertyTypeAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Property type already exists");

        return problem;
    }

    // 404 (if the requested property type id doesn't exist)
    @ExceptionHandler(PropertyTypeNotFoundException.class)
    public ProblemDetail handlePropertyTypeNotFound(PropertyTypeNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Property type not found");

        return problem;
    }

    // 409 (if the property type is still used by properties, it can't be deleted)
    @ExceptionHandler(PropertyTypeInUseException.class)
    public ProblemDetail handlePropertyTypeInUse(PropertyTypeInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Property type in use");

        return problem;
    }

    // 404 (if the requested facility id doesn't exist)
    @ExceptionHandler(FacilityNotFoundException.class)
    public ProblemDetail handleFacilityNotFound(FacilityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Facility not found");

        return problem;
    }


    // 404 (if the requested unit type id doesn't exist)
    @ExceptionHandler(UnitTypeNotFoundException.class)
    public ProblemDetail handleUnitTypeNotFound(UnitTypeNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Unit type not found");

        return problem;
    }

    // 409 (if another facility already uses this code or name)
    @ExceptionHandler(FacilityAlreadyExistsException.class)
    public ProblemDetail handleFacilityAlreadyExists(FacilityAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Facility already exists");

        return problem;
    }

    // 409 (if another unit type already uses this code or name)
    @ExceptionHandler(UnitTypeAlreadyExistsException.class)
    public ProblemDetail handleUnitTypeAlreadyExists(UnitTypeAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Unit type already exists");

        return problem;
    }

    // 409 (if the facility is still used by properties or rental units, so it can't be deleted)
    @ExceptionHandler(FacilityInUseException.class)
    public ProblemDetail handleFacilityInUse(FacilityInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Facility in use");

        return problem;
    }

    // 404 (if the requested availability status id doesn't exist)
    @ExceptionHandler(AvailabilityStatusNotFoundException.class)
    public ProblemDetail handleAvailabilityStatusNotFound(AvailabilityStatusNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Availability status not found");

        return problem;
    }

    // 409 (if the unit type is still used by rental units, so it can't be deleted)
    @ExceptionHandler(UnitTypeInUseException.class)
    public ProblemDetail handleUnitTypeInUse(UnitTypeInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Unit type in use");

        return problem;
    }
}