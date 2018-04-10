package uk.co.autotrader.traverson.conversion;

class StringResourceConverter implements ResourceConverter<String> {

    @Override
    public Class<String> getDestinationType() {
        return String.class;
    }

    @Override
    public String convert(String resourceAsString, Class<? extends String> returnType) {
        return resourceAsString;
    }
}
