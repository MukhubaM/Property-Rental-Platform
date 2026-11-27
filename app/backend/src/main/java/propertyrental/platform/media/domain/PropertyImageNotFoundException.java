package propertyrental.platform.media.domain;

import java.util.UUID;

public class PropertyImageNotFoundException extends RuntimeException {

    public PropertyImageNotFoundException(UUID imageId) {
        super("Property image with id " + imageId + " was not found for this property");
    }
}