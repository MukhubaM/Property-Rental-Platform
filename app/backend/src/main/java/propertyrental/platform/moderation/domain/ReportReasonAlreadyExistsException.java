package propertyrental.platform.moderation.domain;

public class ReportReasonAlreadyExistsException extends RuntimeException {

    public ReportReasonAlreadyExistsException(String field, String value) {
        super("A report reason with " + field + " '" + value + "' already exists");
    }
}
