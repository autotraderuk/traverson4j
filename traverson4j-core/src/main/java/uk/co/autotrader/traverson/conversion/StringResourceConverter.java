package uk.co.autotrader.traverson.conversion;

import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class StringResourceConverter implements ResourceConverter<String> {

    @Override
    public Class<String> getDestinationType() {
        return String.class;
    }

    @Override
    public String convert(InputStream resource, Class<? extends String> returnType) {
        try (InputStream streamToProcess = resource) {
            return new String(streamToProcess.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ConversionException("Failed to convert the input stream to a string", null, e);
        }
    }
}
