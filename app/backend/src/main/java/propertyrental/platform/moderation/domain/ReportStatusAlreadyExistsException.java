package propertyrental.platform.moderation.domain;

public class ReportStatusAlreadyExistsException extends RuntimeException {

    public ReportStatusAlreadyExistsException(String field, String value) {
        super("A report status with " + field + " '" + value + "' already exists");
    }
}