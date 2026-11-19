package propertyrental.platform.moderation.domain;

public class ReportStatusInUseException extends RuntimeException {

    public ReportStatusInUseException(Long id) {
        super("Report status with id " + id + " is still used by listing reports and cannot be deleted");
    }
}
