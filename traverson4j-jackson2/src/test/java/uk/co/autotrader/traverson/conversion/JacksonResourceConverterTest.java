package uk.co.autotrader.traverson.conversion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JacksonResourceConverterTest {

    private JacksonResourceConverter converter;
    @Mock
    private ObjectMapper objectMapper;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        converter = new JacksonResourceConverter();
    }

    @Test
    public void canConvert_ReturnsTrue() throws Exception {
        assertThat(converter.getDestinationType()).isEqualTo(Object.class);
    }

    @Test
    public void convert_GivenJsonStringAndValidStructuredPojo_ReturnsMappedPojo() throws Exception {
        String resourceAsString = Resources.toString(Resources.getResource("hal-embedded.json"), Charset.defaultCharset());

        Domains domains = (Domains) converter.convert(resourceAsString, Domains.class);

        assertThat(domains.getName()).isEqualTo("Domains");
        List<DomainSummary> domainSummaries = domains.getEmbedded().get("domains");
        assertThat(domainSummaries).hasSize(2);
        assertThat(domainSummaries.get(0).getName()).isEqualTo("Other Domain");
        assertThat(domainSummaries.get(1).getName()).isEqualTo("AutoTrader");
        assertThat(domains.getLinks()).hasSize(1).containsKey("self");
        assertThat(domains.getLinks().get("self").getHref()).isEqualTo("http://localhost:8080/domains");
        assertThat(domains.getLinks().get("self").getTemplated()).isFalse();

    }

    @Test
    public void convert_GivenTheObjectMapperThrowsIoException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        IOException ioException = new IOException("Error happened");
        when(objectMapper.readValue(resourceAsString, Domains.class)).thenThrow(ioException);
        expectedException.expect(ConversionException.class);
        expectedException.expectCause(equalTo(ioException));
        expectedException.expect(new ArgumentMatcher() {
            @Override
            public boolean matches(Object item) {
                ConversionException ex = (ConversionException) item;
                return ex.getResourceAsString().equals(resourceAsString);
            }
        });

        JacksonResourceConverter converter = new JacksonResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        converter.convert(resourceAsString, Domains.class);
    }

    @Test
    public void convert_GivenTheObjectMapperThrowsARuntimeException_WrapsInConversionException() throws Exception {
        final String resourceAsString = "{}";
        RuntimeException runtimeException = new RuntimeException("Error happened");
        when(objectMapper.readValue(resourceAsString, Domains.class)).thenThrow(runtimeException);
        expectedException.expect(ConversionException.class);
        expectedException.expectCause(equalTo(runtimeException));
        expectedException.expect(new ArgumentMatcher() {
            @Override
            public boolean matches(Object item) {
                ConversionException ex = (ConversionException) item;
                return ex.getResourceAsString().equals(resourceAsString);
            }
        });

        JacksonResourceConverter converter = new JacksonResourceConverter();
        FieldUtils.writeField(converter, "objectMapper", objectMapper, true);

        converter.convert(resourceAsString, Domains.class);
    }
}