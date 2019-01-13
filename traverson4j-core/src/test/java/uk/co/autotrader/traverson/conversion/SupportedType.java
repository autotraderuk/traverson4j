package uk.co.autotrader.traverson.conversion;

import java.io.InputStream;

class SupportedType {
    private final InputStream value;

    SupportedType(InputStream value) {
        this.value = value;
    }

    public InputStream getValue() {
        return value;
    }
}
