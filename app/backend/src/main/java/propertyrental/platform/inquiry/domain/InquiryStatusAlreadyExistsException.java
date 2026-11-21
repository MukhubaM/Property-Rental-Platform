package propertyrental.platform.inquiry.domain;

public class InquiryStatusAlreadyExistsException extends RuntimeException{

    public InquiryStatusAlreadyExistsException(String field, String value) {
        super("An inquiry status with " + field + " '" + value + "' already exists");
    }
}
