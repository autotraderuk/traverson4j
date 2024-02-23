package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BodyPartEntityConverterTest {

    private MultipartEntityConverter converter;

    @BeforeEach
    void setUp() {
        converter = new MultipartEntityConverter();
    }

    @Test
    void toEntity_GivenTextBody_ReturnsStringEntity() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        HttpEntity entity = converter.toEntity(new SimpleMultipartBody(new SimpleMultipartBody.BodyPart("file", "data".getBytes(StandardCharsets.UTF_8), "contentType", "filename")));

        assertThat(entity.getContentType()).matches(Pattern.compile("multipart/form-data; charset=ISO-8859-1; boundary=.*"));
        entity.writeTo(outputStream);
        String content = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertThat(content)
                .matches(Pattern.compile(".*name=\"file\".*", Pattern.DOTALL | Pattern.MULTILINE))
                .matches(Pattern.compile(".*filename=\"filename\".*", Pattern.DOTALL | Pattern.MULTILINE))
             .matches(Pattern.compile(".*Content-Type: contenttype.*", Pattern.DOTALL | Pattern.MULTILINE))
             .matches(Pattern.compile(".*data.*", Pattern.DOTALL | Pattern.MULTILINE));
    }

    @Test
    void toEntity_GivenTextBodyFromInputStream_ReturnsStringEntity() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        HttpEntity entity = converter.toEntity(new SimpleMultipartBody(new SimpleMultipartBody.BodyPart("file", new ByteArrayInputStream("data".getBytes(StandardCharsets.UTF_8)), "contentType", "filename")));

        assertThat(entity.getContentType()).matches(Pattern.compile("multipart/form-data; charset=ISO-8859-1; boundary=.*"));
        entity.writeTo(outputStream);
        String content = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertThat(content)
                .matches(Pattern.compile(".*name=\"file\".*", Pattern.DOTALL | Pattern.MULTILINE))
                .matches(Pattern.compile(".*filename=\"filename\".*", Pattern.DOTALL | Pattern.MULTILINE))
                .matches(Pattern.compile(".*Content-Type: contenttype.*", Pattern.DOTALL | Pattern.MULTILINE))
                .matches(Pattern.compile(".*data.*", Pattern.DOTALL | Pattern.MULTILINE));
    }

    @Test
    void toEntity_GivenKeyValueBody_ReturnsStringEntity() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        HttpEntity entity = converter.toEntity(new SimpleMultipartBody(new SimpleMultipartBody.BodyPart("key", "value")));

        assertThat(entity.getContentType()).matches(Pattern.compile("multipart/form-data; charset=ISO-8859-1; boundary=.*"));
        entity.writeTo(outputStream);
        String content = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        assertThat(content)
                .matches(Pattern.compile(".*name=\"key\".*", Pattern.DOTALL | Pattern.MULTILINE))
                .matches(Pattern.compile(".*value.*", Pattern.DOTALL | Pattern.MULTILINE));
    }
}
