package uk.co.autotrader.traverson.conversion;

public class SupportedTypeConverter implements ResourceConverter<SupportedType> {

    @Override
    public Class<SupportedType> getDestinationType() {
        return SupportedType.class;
    }

    @Override
    public SupportedType convert(String resourceAsString, Class<? extends SupportedType> returnType) {
        return new SupportedType(resourceAsString);
    }
}
