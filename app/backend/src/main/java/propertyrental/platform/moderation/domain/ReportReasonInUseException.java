package propertyrental.platform.moderation.domain;

public class ReportReasonInUseException extends RuntimeException {

    public ReportReasonInUseException(Long id) {
        super("Report reason with id " + id + " is still used by listing reports and cannot be deleted");
    }
}
