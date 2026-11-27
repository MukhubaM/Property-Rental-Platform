package propertyrental.platform.media.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import propertyrental.platform.media.domain.PropertyImageNotFoundException;
import propertyrental.platform.media.domain.PropertyImageOrderTakenException;
import propertyrental.platform.media.domain.PropertyImagePropertyNotFoundException;

@RestControllerAdvice(basePackageClasses = MediaExceptionHandler.class)
public class MediaExceptionHandler {

    // 404 (the property in the URL doesn't exist)
    @ExceptionHandler(PropertyImagePropertyNotFoundException.class)
    public ProblemDetail handlePropertyNotFound(PropertyImagePropertyNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Property not found");

        return problem;
    }

    // 404 (the image doesn't exist, or belongs to another property)
    @ExceptionHandler(PropertyImageNotFoundException.class)
    public ProblemDetail handleImageNotFound(PropertyImageNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Property image not found");

        return problem;
    }

    // 409 (another image of the property already uses this display order)
    @ExceptionHandler(PropertyImageOrderTakenException.class)
    public ProblemDetail handleOrderTaken(PropertyImageOrderTakenException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Display order already used");

        return problem;
    }

    // 409 (fallback when the database unique constraint catches a duplicate the service check missed)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "A record with the same unique value already exists");
        problem.setTitle("Duplicate record");

        return problem;
    }

    // 400 (the request body failed validation)
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