package uk.co.autotrader.traverson.conversion;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class InputStreamConverterTest {
    private InputStreamConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new InputStreamConverter();
    }

    @Test
    public void getDestinationType_ReturnsInputStreamClass() {
        assertThat(converter.getDestinationType()).isEqualTo(InputStream.class);
    }

    @Test
    public void convert_ReturnsInputStreamAsIs() {
        InputStream expectedStream = Mockito.mock(InputStream.class);

        assertThat(converter.convert(expectedStream, InputStream.class)).isSameAs(expectedStream);
    }
}
