package uk.co.autotrader.traverson.http.multipart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BodyPartTest {
    @Test
    public void init_GivenValues_SetsProperties() {
        SimpleMultipartBody.BodyPart multipart = new SimpleMultipartBody.BodyPart("name", "data".getBytes(StandardCharsets.UTF_8), "contentType", "filename");

        assertThat(multipart.getName()).isEqualTo("name");
        assertThat(multipart.getData()).isEqualTo("data".getBytes(StandardCharsets.UTF_8));
        assertThat(multipart.getContentType()).isEqualTo("contentType");
        assertThat(multipart.getFilename()).isEqualTo("filename");
    }
}
