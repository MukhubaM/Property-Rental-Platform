package propertyrental.platform.media.domain;

public class PropertyImageOrderTakenException extends RuntimeException {

    public PropertyImageOrderTakenException(Integer displayOrder) {
        super("Display order " + displayOrder + " is already used by another image of this property");
    }
}