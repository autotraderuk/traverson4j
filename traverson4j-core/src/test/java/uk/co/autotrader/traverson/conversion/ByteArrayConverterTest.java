package uk.co.autotrader.traverson.conversion;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ByteArrayConverterTest {
    private ByteArrayConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new ByteArrayConverter();
    }

    @Test
    public void getDestinationType_ReturnsByteArrayClass() throws Exception {
        assertThat(converter.getDestinationType()).isEqualTo(byte[].class);
    }

    @Test
    public void convert_ReturnsTheInputString() throws Exception {
        byte[] bytes = new byte[] {1, 2, 3};
        InputStream inputStream = Mockito.spy(new ByteArrayInputStream(bytes));

        assertThat(converter.convert(inputStream, byte[].class)).isEqualTo(bytes);
        verify(inputStream).close();
    }

    @Test
    public void convert_WrapsIOExceptionInConversionException() throws Exception {
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(inputStream.read(any())).thenThrow(new IOException());

        assertThatThrownBy(() -> converter.convert(inputStream, byte[].class))
                .isInstanceOf(ConversionException.class)
                .hasMessage("Failed to convert the input stream to a byte array");
        verify(inputStream).close();
    }
}
