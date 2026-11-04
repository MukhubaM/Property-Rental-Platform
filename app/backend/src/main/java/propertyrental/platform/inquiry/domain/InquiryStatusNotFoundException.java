package propertyrental.platform.inquiry.domain;

public class InquiryStatusNotFoundException extends RuntimeException {

    public InquiryStatusNotFoundException(Long id) {
        super("Inquiry status with id " + id + " was not found");
    }
}