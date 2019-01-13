package uk.co.autotrader.traverson.conversion;

import org.apache.commons.io.IOUtils;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.IOException;
import java.io.InputStream;

class ByteArrayConverter implements ResourceConverter<byte[]> {
    @Override
    public Class<byte[]> getDestinationType() {
        return byte[].class;
    }

    @Override
    public byte[] convert(InputStream resource, Class<? extends byte[]> returnType) {
        try (InputStream streamToProcess = resource) {
            return IOUtils.toByteArray(streamToProcess);
        } catch (IOException e) {
            throw new ConversionException("Failed to convert the input stream to a byte array", null,  e);
        }
    }
}
