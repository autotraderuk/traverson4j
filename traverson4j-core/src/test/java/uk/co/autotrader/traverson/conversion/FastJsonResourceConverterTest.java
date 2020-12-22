package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FastJsonResourceConverterTest {

    private FastJsonResourceConverter converter;

    @Before
    public void setUp() {
        converter = new FastJsonResourceConverter();
    }

    @Test
    public void getDestinationType_ReturnsJSONObject() {
        assertThat(converter.getDestinationType()).isEqualTo(JSONObject.class);
    }

    @Test
    public void convert_GivenJsonString_ParsesJsonCorrectly() {
        String resourceAsString = "{'name':'test', 'anotherName':'comes before the first one alphabetically'}";

        JSONObject resource = converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), JSONObject.class);

        assertThat(resource).isNotNull().containsExactly(MapEntry.entry("name", "test"), MapEntry.entry("anotherName", "comes before the first one alphabetically"));
    }

    @Test
    public void convert_GivenXMLString_ThrowsConversionException() {
        final String resourceAsString = "<xml><_links><self><href>http://localhost</href></self></_links></xml>";

        assertThatThrownBy(() -> converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), JSONObject.class))
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

        JSONObject resource = converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), JSONObject.class);

        assertThat(resource).isNull();
    }

    @Test
    public void convert_GivenNullString_ReturnNull() {
        String resourceAsString = "";

        JSONObject resource = converter.convert(IOUtils.toInputStream(resourceAsString, StandardCharsets.UTF_8), JSONObject.class);

        assertThat(resource).isNull();
    }
}
