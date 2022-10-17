package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import uk.co.autotrader.traverson.exception.ConversionException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FastJsonArrayResourceConverterTest {

    private FastJsonArrayResourceConverter converter;

    @Before
    public void setUp() {
        converter = new FastJsonArrayResourceConverter();
    }

    @Test
    public void getDestinationType_ReturnsJSONObject() {
        assertThat(converter.getDestinationType()).isEqualTo(JSONArray.class);
    }

    @Test
    public void convert_GivenJsonString_ParsesJsonCorrectly() {
        String resourceAsString = "[{'name':'one'},{'name':'two'}]";

        JSONArray resource = converter.convert(toInputStream(resourceAsString), JSONArray.class);

        assertThat(resource).isEqualTo(
                new JSONArray()
                        .fluentAdd(new JSONObject().fluentPut("name", "one"))
                        .fluentAdd(new JSONObject().fluentPut("name", "two")));
    }

    @Test
    public void convert_GivenXMLString_ThrowsConversionException() {
        final String resourceAsString = "<xml><_links><self><href>http://localhost</href></self></_links></xml>";

        assertThatThrownBy(() -> converter.convert(toInputStream(resourceAsString), JSONArray.class))
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

        JSONArray resource = converter.convert(toInputStream(resourceAsString), JSONArray.class);

        assertThat(resource).isNull();
    }

    private InputStream toInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }
}

