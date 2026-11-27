package propertyrental.platform.media.domain;

import java.util.UUID;

// Thrown when the property in the URL does not exist (returned as 404)
public class PropertyImagePropertyNotFoundException extends RuntimeException {

    public PropertyImagePropertyNotFoundException(UUID propertyId) {
        super("Property with id " + propertyId + " was not found");
    }
}
