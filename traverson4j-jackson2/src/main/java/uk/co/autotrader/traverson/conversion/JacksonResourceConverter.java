package uk.co.autotrader.traverson.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;

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
    public Object convert(InputStream resource, Class<? extends Object> returnType) {
        String resourceAsString = null;
        try {
            resourceAsString = new StringResourceConverter().convert(resource, String.class);
            return objectMapper.readValue(resourceAsString, returnType);
        } catch (JsonProcessingException | RuntimeException e) {
            throw new ConversionException("Failed to map object using jackson", resourceAsString, e);
        }
    }

}
