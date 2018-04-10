package uk.co.autotrader.traverson.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.IOException;

public class JacksonResourceConverter implements ResourceConverter<Object> {

    private final ObjectMapper objectMapper;

    public JacksonResourceConverter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Class<Object> getDestinationType() {
        return Object.class;
    }

    @Override
    public Object convert(String resourceAsString, Class<? extends Object> returnType) {
        try {
            return objectMapper.readValue(resourceAsString, returnType);
        } catch (IOException e) {
            throw new ConversionException("Failed to map object using jackson", resourceAsString, e);
        } catch (RuntimeException e) {
            throw new ConversionException("Failed to map object using jackson", resourceAsString, e);
        }
    }

}
