package propertyrental.platform.identity.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import propertyrental.platform.identity.domain.RoleNotFoundException;

@RestControllerAdvice(basePackageClasses = IdentityExceptionHandler.class)
public class IdentityExceptionHandler {

    @ExceptionHandler(RoleNotFoundException.class)
    public ProblemDetail handleRoleNotFound(RoleNotFoundException ex) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Role not found");

        return problem;
    }
}
