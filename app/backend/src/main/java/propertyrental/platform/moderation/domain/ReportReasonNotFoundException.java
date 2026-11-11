package propertyrental.platform.moderation.domain;

public class ReportReasonNotFoundException extends RuntimeException {

    public ReportReasonNotFoundException(Long id) {
        super("Report reason with id " + id + " was not found");
    }
}
