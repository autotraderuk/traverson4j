package uk.co.autotrader.traverson.conversion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ByteArrayConverterTest {
    private ByteArrayConverter converter;

    @BeforeEach
    void setUp() {
        converter = new ByteArrayConverter();
    }

    @Test
    void getDestinationType_ReturnsByteArrayClass() {
        assertThat(converter.getDestinationType()).isEqualTo(byte[].class);
    }

    @Test
    void convert_ReturnsTheInputString() throws Exception {
        byte[] bytes = new byte[] {1, 2, 3};
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(bytes));

        assertThat(converter.convert(inputStream, byte[].class)).isEqualTo(bytes);
        verify(inputStream).close();
    }

    @Test
    void convert_WrapsIOExceptionInConversionException() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(inputStream.readAllBytes()).thenThrow(new IOException());

        assertThatThrownBy(() -> converter.convert(inputStream, byte[].class))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Failed to convert the input stream to a byte array");
        verify(inputStream).close();
    }
}
