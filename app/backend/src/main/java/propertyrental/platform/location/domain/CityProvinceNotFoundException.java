package propertyrental.platform.location.domain;

// This is thrown when a create or update request names a province that does not exist (returned as 400)
public class CityProvinceNotFoundException extends RuntimeException {

    public CityProvinceNotFoundException(Long provinceId) {
        super("Province with id " + provinceId + " does not exist");
    }
}