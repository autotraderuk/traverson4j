package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.HttpEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BodyPartEntityConverterTest {

    private MultipartEntityConverter converter;

    @Before
    public void setUp() {
        converter = new MultipartEntityConverter();
    }

    @Test
    public void toEntity_GivenTextBody_ReturnsStringEntity() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        HttpEntity entity = converter.toEntity(new SimpleMultipartBody(new SimpleMultipartBody.BodyPart("file", "data".getBytes(StandardCharsets.UTF_8), "contentType", "filename")));

        assertThat(entity.getContentType()).matches(Pattern.compile("multipart/form-data; charset=.* boundary=.*"));
        entity.writeTo(outputStream);
        String content = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertThat(content).matches(Pattern.compile(".*name=\"file\".*", Pattern.DOTALL | Pattern.MULTILINE));
        assertThat(content).matches(Pattern.compile(".*filename=\"filename\".*", Pattern.DOTALL | Pattern.MULTILINE));
        assertThat(content).matches(Pattern.compile(".*Content-Type: contenttype.*", Pattern.DOTALL | Pattern.MULTILINE));
        assertThat(content).matches(Pattern.compile(".*data.*", Pattern.DOTALL | Pattern.MULTILINE));
    }
}
