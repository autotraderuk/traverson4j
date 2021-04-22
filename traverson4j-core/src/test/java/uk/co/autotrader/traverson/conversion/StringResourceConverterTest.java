package uk.co.autotrader.traverson.conversion;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StringResourceConverterTest {

    private StringResourceConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new StringResourceConverter();
    }

    @Test
    public void getDestinationType_ReturnsString() throws Exception {
        assertThat(converter.getDestinationType()).isEqualTo(String.class);
    }

    @Test
    public void convert_ReturnsTheInputString() throws Exception {
        String resourceAsString = "My Resource";
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8)));

        assertThat(converter.convert(inputStream, String.class)).isEqualTo(resourceAsString);
        verify(inputStream).close();
    }

    @Test
    public void convert_WrapsIOExceptionInConversionException() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException());

        assertThatThrownBy(() -> converter.convert(inputStream, String.class))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Failed to convert the input stream to a string");
        verify(inputStream).close();
    }
}
