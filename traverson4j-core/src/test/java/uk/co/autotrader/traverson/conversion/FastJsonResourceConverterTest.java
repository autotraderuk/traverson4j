package uk.co.autotrader.traverson.conversion;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.assertj.core.data.MapEntry;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import uk.co.autotrader.traverson.exception.ConversionException;

import static org.assertj.core.api.Assertions.assertThat;

public class FastJsonResourceConverterTest {

    private FastJsonResourceConverter converter;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        converter = new FastJsonResourceConverter();
    }

    @Test
    public void canConvert_GivenJSONObjectClass_ReturnsFalse() throws Exception {
        assertThat(converter.getDestinationType()).isEqualTo(JSONObject.class);
    }

    @Test
    public void convert_GivenJsonString_ParsesJsonCorrectly() throws Exception {
        String resourceAsString = "{'name':'test', 'anotherName':'comes before the first one alphabetically'}";

        JSONObject resource = converter.convert(resourceAsString, JSONObject.class);

        assertThat(resource).isNotNull().containsExactly(MapEntry.entry("name", "test"), MapEntry.entry("anotherName", "comes before the first one alphabetically"));
    }

    @Test
    public void convert_GivenXMLString_ThrowsConversionException() throws Exception {
        final String resourceAsString = "<xml><_links><self><href>http://localhost</href></self></_links></xml>";
        expectedException.expect(ConversionException.class);
        expectedException.expectCause(IsInstanceOf.<Throwable>instanceOf(JSONException.class));
        expectedException.expect(new ArgumentMatcher() {
            @Override
            public boolean matches(Object item) {
                ConversionException ex = (ConversionException) item;
                return ex.getResourceAsString().equals(resourceAsString);
            }
        });

        converter.convert(resourceAsString, JSONObject.class);
    }

    @Test
    public void convert_GivenEmptyString_ReturnNull() throws Exception {
        String resourceAsString = "";

        JSONObject resource = converter.convert(resourceAsString, JSONObject.class);

        assertThat(resource).isNull();
    }

    @Test
    public void convert_GivenNullString_ReturnNull() throws Exception {
        String resourceAsString = "";

        JSONObject resource = converter.convert(resourceAsString, JSONObject.class);

        assertThat(resource).isNull();
    }
}