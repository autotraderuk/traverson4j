package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONObject;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceConversionServiceTest {

    private final ResourceConversionService service = ResourceConversionService.getInstance();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    private ResourceConverter<Number> converter;
    @Mock
    private ResourceConverter<Object> failingConverter;
    @Mock
    private InputStream inputStream;

    @Test
    public void init_EnsuresThatTheDefaultConvertersAreRegistered() {
        Map<Class<?>, ResourceConverter<?>> converters = service.getConvertersByClass();

        assertThat(converters).isNotEmpty();
        assertThat(converters.values()).extracting("class").contains(FastJsonResourceConverter.class, StringResourceConverter.class, ByteArrayConverter.class, InputStreamConverter.class);
    }

    @Test
    public void convert_GivenUnsupportedResponseType_ThrowsException() {
        expectedException.expect(ConversionException.class);
        expectedException.expectCause(CoreMatchers.nullValue(Throwable.class));
        expectedException.expectMessage("Unsupported return type of uk.co.autotrader.traverson.conversion.UnsupportedType");

        service.convert(inputStream, UnsupportedType.class);
    }

    @Test
    public void addConverter_RegistersTheConverterForUseLater() {

        SupportedType result = service.convert(inputStream, SupportedType.class);

        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(inputStream);
    }

    @Test
    public void convert_GivenRequestForFastJSON_EnsuresTheFastJsonConverterIsLoaded() {
        String resourceAsString = "{'name':'test'}";

        JSONObject resource = service.convert(toInputStream(resourceAsString), JSONObject.class);

        assertThat(resource).isNotNull().containsEntry("name", "test");
    }


    @Test
    public void convert_GivenRequestForString_EnsuresTheStringConverterIsLoaded() {
        String resourceAsString = "{'name':'test'}";
        String resource = service.convert(toInputStream(resourceAsString), String.class);

        assertThat(resource).isNotNull().isEqualTo(resourceAsString);
    }

    @Test
    public void convert_GivenRequestForByteArray_EnsuresTheStringConverterIsLoaded() {
        byte[] bytes = new byte[] {0, 1, 2, 3};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        byte[] resource = service.convert(inputStream, byte[].class);

        assertThat(resource).isNotNull().isEqualTo(bytes);
    }

    @Test
    public void convert_GivenTheConvertersAreLoadedInAnyOrder_TheConversionServiceWillTraverseTheClassHierarchyUntilAMatch() {
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
