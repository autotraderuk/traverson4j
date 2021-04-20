package uk.co.autotrader.traverson.conversion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JacksonResourceConverterTest {

    private JacksonResourceConverter converter;
    @Mock
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        converter = new JacksonResourceConverter();
    }

    @Test
    public void canConvert_ReturnsTrue() {
        assertThat(converter.getDestinationType()).isEqualTo(Object.class);
    }

    @Test
    public void convert_GivenJsonStringAndValidStructuredPojo_ReturnsMappedPojo() throws Exception {
        String resourceAsString = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());
        InputStream resource = Mockito.spy(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8));

        Domains domains = (Domains) converter.convert(resource, Domains.class);

        assertThat(domains.getName()).isEqualTo("Domains");
        List<DomainSummary> domainSummaries = domains.getEmbedded().get("domains");
        assertThat(domainSummaries).hasSize(2);
        assertThat(domainSummaries.get(0).getName()).isEqualTo("Other Domain");
        assertThat(domainSummaries.get(1).getName()).isEqualTo("AutoTrader");
        assertThat(domains.getLinks()).hasSize(1).containsKey("self");
        assertThat(domains.getLinks().get("self").getHref()).isEqualTo("http://localhost:8080/domains");
        assertThat(domains.getLinks().get("self").getTemplated()).isFalse();
        verify(resource).close();
    }

    @Test
    public void convert_GivenTheObjectMapperThrowsJacksonException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        JsonProcessingException jsonProcessingException = Mockito.mock(JsonProcessingException.class);
        when(objectMapper.readValue(resourceAsString, Domains.class)).thenThrow(jsonProcessingException);

        JacksonResourceConverter converter = new JacksonResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        assertThatThrownBy(() -> converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), Domains.class))
                .isInstanceOf(ConversionException.class)
                .hasCauseInstanceOf(JsonProcessingException.class)
                .matches(object -> {
                    ConversionException ex = (ConversionException) object;
                    return ex.getResourceAsString().equals(resourceAsString);
                });
    }

    @Test
    public void convert_GivenTheObjectMapperThrowsARuntimeException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        RuntimeException runtimeException = new RuntimeException("Error happened");
        when(objectMapper.readValue(resourceAsString, Domains.class)).thenThrow(runtimeException);

        JacksonResourceConverter converter = new JacksonResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        assertThatThrownBy(() -> converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), Domains.class))
                .isInstanceOf(ConversionException.class)
                .hasCauseInstanceOf(RuntimeException.class)
                .matches(object -> {
                    ConversionException ex = (ConversionException) object;
                    return ex.getResourceAsString().equals(resourceAsString);
                });
    }
}
