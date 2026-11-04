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
                HttpStatus.CONFLICT, "A listing status with this code or name already exists");
        problem.setTitle("Listing status already exists");

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
}