package uk.co.autotrader.traverson.exception;

/**
 * <p>Thrown when the {@link uk.co.autotrader.traverson.conversion.ResourceConversionService} fails
 * to convert the returned resource to the requested type</p>
 * <p>This could be due to no compatible {@link uk.co.autotrader.traverson.conversion.ResourceConverter} for the requested type
 *  or the selected {@link uk.co.autotrader.traverson.conversion.ResourceConverter} errors when attempting to convert the returned resource
 * </p>
 */
public class ConversionException extends RuntimeException {
    private final String resourceAsString;

    /**
     * Constructs a ConversionException with a suitable message
     * @param message the detailed message
     */
    public ConversionException(String message) {
        this(message, null);
    }

    /**
     * Constructs a ConversionException with a suitable message and the resource attempted to be converted
     * @param message the detailed message
     * @param resourceAsString a text representation of the returned resource
     */
    public ConversionException(String message, String resourceAsString) {
        this(message, resourceAsString, null);
    }

    /**
     * Constructs a ConversionException with a suitable message, resource attempted to be converted and the exception thrown
     * @param message the detailed message
     * @param resourceAsString a text representation of the returned resource
     * @param cause the original exception when attempting the conversion
     */
    public ConversionException(String message, String resourceAsString, Throwable cause) {
        super(message, cause);
        this.resourceAsString = resourceAsString;
    }

    /**
     * @return a text representation of the returned resource
     */
    public String getResourceAsString() {
        return resourceAsString;
    }
}
