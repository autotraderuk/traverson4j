package uk.co.autotrader.traverson.conversion;

import tools.jackson.databind.json.JsonMapper;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;

public class Jackson3ResourceConverter implements ResourceConverter<Object> {

    private final JsonMapper objectMapper;

    public Jackson3ResourceConverter() {
        this.objectMapper = JsonMapper.builder().build();
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
        } catch (RuntimeException e) {
            throw new ConversionException("Failed to map object using jackson", resourceAsString, e);
        }
    }

}
