package propertyrental.platform.inquiry.domain;

public class InquiryStatusInUseException extends RuntimeException {

    public InquiryStatusInUseException(Long id) {
        super("Inquiry status with id " + id + " is still used by inquiries and cannot be deleted");
    }
}