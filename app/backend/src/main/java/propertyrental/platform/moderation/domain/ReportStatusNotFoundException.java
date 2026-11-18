package propertyrental.platform.moderation.domain;

public class ReportStatusNotFoundException extends RuntimeException {

    public ReportStatusNotFoundException(Long id) {
        super("Report status with id " + id + " was not found");
    }
}
