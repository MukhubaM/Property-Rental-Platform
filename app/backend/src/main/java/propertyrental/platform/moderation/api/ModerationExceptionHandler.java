package propertyrental.platform.moderation.api;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import propertyrental.platform.moderation.domain.ReportReasonAlreadyExistsException;
import propertyrental.platform.moderation.domain.ReportReasonInUseException;
import propertyrental.platform.moderation.domain.ReportReasonNotFoundException;

@RestControllerAdvice(basePackageClasses = ModerationExceptionHandler.class)
public class ModerationExceptionHandler {

    // 404 (the requested report reason id doesn't exist)
    @ExceptionHandler(ReportReasonNotFoundException.class)
    public ProblemDetail handleReportReasonNotFound(ReportReasonNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Report reason not found");

        return problem;
    }

    // 409 (another report reason already uses this code or name)
    @ExceptionHandler(ReportReasonAlreadyExistsException.class)
    public ProblemDetail handleReportReasonAlreadyExists(ReportReasonAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Report reason already exists");

        return problem;
    }

    // 409 (if the report reason is still used by listing reports, so it can't be deleted)
    @ExceptionHandler(ReportReasonInUseException.class)
    public ProblemDetail handleReportReasonInUse(ReportReasonInUseException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Report reason in use");

        return problem;
    }

    // 409 (fallback when the database unique constraint catches a duplicate the service check missed)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, "A record with this code or name already exists");
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
}