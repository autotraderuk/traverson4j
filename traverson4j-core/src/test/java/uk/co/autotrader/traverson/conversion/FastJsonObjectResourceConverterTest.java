package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FastJsonObjectResourceConverterTest {

    private FastJsonObjectResourceConverter converter;

    @Before
    public void setUp() {
        converter = new FastJsonObjectResourceConverter();
    }

    @Test
    public void getDestinationType_ReturnsJSONObject() {
        assertThat(converter.getDestinationType()).isEqualTo(JSONObject.class);
    }

    @Test
    public void convert_GivenJsonString_ParsesJsonCorrectly() {
        String resourceAsString = "{'name':'test', 'anotherName':'comes before the first one alphabetically'}";

        JSONObject resource = converter.convert(toInputStream(resourceAsString), JSONObject.class);

        assertThat(resource).isNotNull().containsExactly(MapEntry.entry("name", "test"), MapEntry.entry("anotherName", "comes before the first one alphabetically"));
    }

    @Test
    public void convert_GivenXMLString_ThrowsConversionException() {
        final String resourceAsString = "<xml><_links><self><href>http://localhost</href></self></_links></xml>";

        assertThatThrownBy(() -> converter.convert(toInputStream(resourceAsString), JSONObject.class))
                .isInstanceOf(ConversionException.class)
                .hasCauseInstanceOf(JSONException.class)
                .matches(object -> {
                    ConversionException ex = (ConversionException) object;
                    return ex.getResourceAsString().equals(resourceAsString);
                });
    }

    @Test
    public void convert_GivenEmptyString_ReturnNull() {
        String resourceAsString = "";

        JSONObject resource = converter.convert(toInputStream(resourceAsString), JSONObject.class);

        assertThat(resource).isNull();
    }

    private InputStream toInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}
