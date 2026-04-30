package uk.co.autotrader.traverson.conversion;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Jackson3ResourceConverterTest {

    private Jackson3ResourceConverter converter;
    @Mock
    private JsonMapper objectMapper;

    @BeforeEach
    void setUp() {
        converter = new Jackson3ResourceConverter();
    }

    @Test
    void canConvert_ReturnsTrue() {
        assertThat(converter.getDestinationType()).isEqualTo(Object.class);
    }

    @Test
    void convert_GivenJsonStringAndValidStructuredPojo_ReturnsMappedPojo() throws Exception {
        final String resourceAsString = "{\"name\":\"Domains\"}";
        InputStream resource = Mockito.spy(new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8)));

        Domain domain = (Domain) converter.convert(resource, Domain.class);

        assertThat(domain.name()).isEqualTo("Domains");
        verify(resource).close();
    }

    @Test
    void convert_GivenTheObjectMapperThrowsJacksonException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        JacksonException jacksonException = Mockito.mock(JacksonException.class);
        when(objectMapper.readValue(resourceAsString, Domain.class)).thenThrow(jacksonException);

        Jackson3ResourceConverter converter = new Jackson3ResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        assertThatThrownBy(() -> converter.convert(new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8)), Domain.class))
                .isInstanceOf(ConversionException.class)
                .hasCauseInstanceOf(JacksonException.class)
                .matches(object -> {
                    ConversionException ex = (ConversionException) object;
                    return ex.getResourceAsString().equals(resourceAsString);
                });
    }

    @Test
    void convert_GivenTheObjectMapperThrowsARuntimeException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        RuntimeException runtimeException = new RuntimeException("Error happened");
        when(objectMapper.readValue(resourceAsString, Domain.class)).thenThrow(runtimeException);

        Jackson3ResourceConverter converter = new Jackson3ResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        assertThatThrownBy(() -> converter.convert(new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8)), Domain.class))
                .isInstanceOf(ConversionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .matches(object -> {
                    ConversionException ex = (ConversionException) object;
                    return ex.getResourceAsString().equals(resourceAsString);
                });
    }

    private record Domain(String name) {
    }
}
