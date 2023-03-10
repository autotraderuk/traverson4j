package uk.co.autotrader.traverson.http.multipart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class BodyPartTest {
    @Test
    void init_GivenValues_SetsProperties() {
        SimpleMultipartBody.BodyPart multipart = new SimpleMultipartBody.BodyPart("name", "data".getBytes(StandardCharsets.UTF_8), "contentType", "filename");

        assertThat(multipart.getName()).isEqualTo("name");
        assertThat(multipart.getData()).isEqualTo("data".getBytes(StandardCharsets.UTF_8));
        assertThat(multipart.getContentType()).isEqualTo("contentType");
        assertThat(multipart.getFilename()).isEqualTo("filename");
    }
}
