package propertyrental.platform.inquiry.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import propertyrental.platform.inquiry.domain.InquiryStatusNotFoundException;

@RestControllerAdvice(basePackageClasses = InquiryExceptionHandler.class)
public class InquiryExceptionHandler {

    // 404 (the requested inquiry status id doesn't exist)
    @ExceptionHandler(InquiryStatusNotFoundException.class)
    public ProblemDetail handleNotFound(InquiryStatusNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Inquiry status not found");

        return problem;
    }
}