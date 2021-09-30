package uk.co.autotrader.traverson.conversion;

import java.io.InputStream;

/**
 * This class does not close the inputStream, relying on the developers to do so
 */
class InputStreamConverter implements ResourceConverter<InputStream> {
    @Override
    public Class<InputStream> getDestinationType() {
        return InputStream.class;
    }

    @Override
    public InputStream convert(InputStream resource, Class<? extends InputStream> returnType) {
        return resource;
    }
}
