package propertyrental.platform.location.domain;

// This is thrown when a create or update request names a city that does not exist (returned as 400)
public class SuburbCityNotFoundException extends RuntimeException {

    public SuburbCityNotFoundException(Long cityId) {
        super("City with id " + cityId + " does not exist");
    }
}
