package uk.co.autotrader.traverson.conversion;

import java.io.InputStream;

public class SupportedTypeConverter implements ResourceConverter<SupportedType> {

    @Override
    public Class<SupportedType> getDestinationType() {
        return SupportedType.class;
    }

    @Override
    public SupportedType convert(InputStream resource, Class<? extends SupportedType> returnType) {
        return new SupportedType(resource);
    }
}
