package uk.co.autotrader.traverson.conversion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class InputStreamConverterTest {
    private InputStreamConverter converter;

    @BeforeEach
    void setUp() {
        converter = new InputStreamConverter();
    }

    @Test
    void getDestinationType_ReturnsInputStreamClass() {
        assertThat(converter.getDestinationType()).isEqualTo(InputStream.class);
    }

    @Test
    void convert_ReturnsInputStreamAsIs() {
        InputStream expectedStream = Mockito.mock(InputStream.class);

        assertThat(converter.convert(expectedStream, InputStream.class)).isSameAs(expectedStream);
    }
}
