package propertyrental.platform.location.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import propertyrental.platform.location.domain.ProvinceAlreadyExistsException;
import propertyrental.platform.location.domain.ProvinceInUseException;
import propertyrental.platform.location.domain.ProvinceNotFoundException;

@RestControllerAdvice(basePackageClasses = LocationExceptionHandler.class)
public class LocationExceptionHandler {

    // 404 (if the requested province id doesn't exist)
    @ExceptionHandler(ProvinceNotFoundException.class)
    public ProblemDetail handleNotFound(ProvinceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Province not found");

        return problem;
    }

    // 409 (if another province already uses this code or name)
    @ExceptionHandler(ProvinceAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(ProvinceAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Province already exists");

        return problem;
    }

    // 409 (if the province is still used by cities or properties, it can't be deleted)
    @ExceptionHandler(ProvinceInUseException.class)
    public ProblemDetail handleInUse(ProvinceInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Province in use");

        return problem;
    }

    // 409 (this fallback when the database unique constraint catches a duplicate the service check missed)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "A province with this code or name already exists");
        problem.setTitle("Province already exists");

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