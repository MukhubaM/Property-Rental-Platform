package propertyrental.platform.inquiry.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;
import propertyrental.platform.inquiry.domain.InquiryStatusAlreadyExistsException;

@RestControllerAdvice(basePackageClasses = InquiryExceptionHandler.class)
public class InquiryExceptionHandler {

    // 404 (if the requested inquiry status id doesn't exist)
    @ExceptionHandler(InquiryStatusNotFoundException.class)
    public ProblemDetail handleNotFound(InquiryStatusNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Inquiry status not found");

        return problem;
    }

    // 409 (if another inquiry status already uses this code or name)
    @ExceptionHandler(InquiryStatusAlreadyExistsException.class)
    public ProblemDetail handleAlreadyExists(InquiryStatusAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Inquiry status already exists");

        return problem;
    }

    // 409 (this fallback when the database unique constraint catches a duplicate the service check missed)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "A record with this code or name already exists");
        problem.setTitle("Duplicate record");

        return problem;
    }

    // 400 (if the request body failed validation [blank or too long fields])
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