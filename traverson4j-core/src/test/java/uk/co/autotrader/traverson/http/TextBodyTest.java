package uk.co.autotrader.traverson.http;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TextBodyTest {

    @Test
    void init_GivenValues_SetsProperties() {
        TextBody body = new TextBody("data", "contentType", Charsets.UTF_8);

        assertThat(body.getContent()).isEqualTo("data");
        assertThat(body.getContentType()).isEqualTo("contentType");
        assertThat(body.getCharset()).isEqualTo(Charsets.UTF_8);
    }

    @Test
    void legacyinit_GivenValues_SetsProperties() {
        TextBody body = new TextBody("data", "contentType");

        assertThat(body.getContent()).isEqualTo("data");
        assertThat(body.getContentType()).isEqualTo("contentType");
        assertThat(body.getCharset()).isNull();
    }
}
