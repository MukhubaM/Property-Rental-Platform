package propertyrental.platform.location.domain;

public class ProvinceNotFoundException extends RuntimeException {

    public ProvinceNotFoundException(Long id) {
        super("Province with id " + id + " was not found");
    }
}