package uk.co.autotrader.traverson.conversion;

import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;

/**
 * <p>Register instances via META-INF/services/uk.co.autotrader.traverson.conversion.ResourceConverter Service loader config </p>
 * <p>Or register them via ResourceConversionService.addConverter()</p>
 * <p>Classes implementing this should be thread safe</p>
 * @param <T> return type
 */
public interface ResourceConverter<T> {
    /**
     * @return Destination Class that this converter will support
     */
    Class<T> getDestinationType();

    /**
     * Should only ever throw {@link uk.co.autotrader.traverson.exception.ConversionException}
     *
     * @param resource  An InputStream of the resource wanting to be converted. This class should close the inputStream, unless explicitly documented
     * @param returnType the class for the returned object
     * @throws ConversionException if the conversion fails
     * @return instance of returnType
     */
    T convert(InputStream resource, Class<? extends T> returnType);
}
