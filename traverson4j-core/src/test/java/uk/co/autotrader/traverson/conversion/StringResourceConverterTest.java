package uk.co.autotrader.traverson.conversion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

class StringResourceConverterTest {

    private StringResourceConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StringResourceConverter();
    }

    @Test
    void getDestinationType_ReturnsString() {
        assertThat(converter.getDestinationType()).isEqualTo(String.class);
    }

    @Test
    void convert_ReturnsTheInputString() throws Exception {
        String resourceAsString = "My Resource";
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8)));

        assertThat(converter.convert(inputStream, String.class)).isEqualTo(resourceAsString);
        verify(inputStream).close();
    }

    @Test
    void convert_WrapsIOExceptionInConversionException() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException());

        assertThatThrownBy(() -> converter.convert(inputStream, String.class))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Failed to convert the input stream to a string");
        verify(inputStream).close();
    }
}
