package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceConversionServiceTest {

    private final ResourceConversionService service = ResourceConversionService.getInstance();
    @Mock
    private ResourceConverter<Number> converter;
    @Mock
    private ResourceConverter<Object> failingConverter;
    @Mock
    private InputStream inputStream;

    @Test
    void init_EnsuresThatTheDefaultConvertersAreRegistered() {
        Map<Class<?>, ResourceConverter<?>> converters = service.getConvertersByClass();

        assertThat(converters).isNotEmpty();
        assertThat(converters.values()).extracting("class").contains(FastJsonResourceConverter.class, StringResourceConverter.class, ByteArrayConverter.class, InputStreamConverter.class);
    }

    @Test
    void convert_GivenUnsupportedResponseType_ThrowsException() {
        ConversionException exception = Assertions.assertThrows(ConversionException.class, () -> {
            service.convert(inputStream, UnsupportedType.class);
        });

        assertThat(exception.getCause()).isNull();
        assertThat(exception.getMessage()).isEqualTo("Unsupported return type of uk.co.autotrader.traverson.conversion.UnsupportedType");
    }

    @Test
    void addConverter_RegistersTheConverterForUseLater() {

        SupportedType result = service.convert(inputStream, SupportedType.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(inputStream);
    }

    @Test
    void convert_GivenRequestForFastJSON_EnsuresTheFastJsonConverterIsLoaded() {
        String resourceAsString = "{\"name\":\"test\"}";

        JSONObject resource = service.convert(toInputStream(resourceAsString), JSONObject.class);

        assertThat(resource).isNotNull().containsEntry("name", "test");
    }


    @Test
    void convert_GivenRequestForString_EnsuresTheStringConverterIsLoaded() {
        String resourceAsString = "{'name':'test'}";
        String resource = service.convert(toInputStream(resourceAsString), String.class);

        assertThat(resource).isNotNull().isEqualTo(resourceAsString);
    }

    @Test
    void convert_GivenRequestForByteArray_EnsuresTheStringConverterIsLoaded() {
        byte[] bytes = new byte[] {0, 1, 2, 3};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        byte[] resource = service.convert(inputStream, byte[].class);

        assertThat(resource).isNotNull().isEqualTo(bytes);
    }

    @Test
    void convert_GivenTheConvertersAreLoadedInAnyOrder_TheConversionServiceWillTraverseTheClassHierarchyUntilAMatch() {
        String resourceAsString = "1234";
        InputStream resourceStream = toInputStream(resourceAsString);
        when(failingConverter.getDestinationType()).thenReturn(Object.class);
        when(converter.getDestinationType()).thenReturn(Number.class);
        when(converter.convert(resourceStream, Integer.class)).thenReturn(1234);
        Map<Class<?>, ResourceConverter<?>> converters = new LinkedHashMap<Class<?>, ResourceConverter<?>>();
        converters.put(failingConverter.getDestinationType(), failingConverter);
        converters.put(converter.getDestinationType(), converter);
        ResourceConversionService service = new ResourceConversionService(converters);

        Integer value = service.convert(resourceStream, Integer.class);

        assertThat(value).isEqualTo(1234);
    }

    private InputStream toInputStream(String resourceAsString) {
        return new ByteArrayInputStream(resourceAsString.getBytes(StandardCharsets.UTF_8));
    }
}
