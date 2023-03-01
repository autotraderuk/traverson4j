package uk.co.autotrader.traverson.http;

import com.google.common.base.Charsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FormBodyTest {

    @Test
    void init_GivenValues_SetsProperties() {

        FormDataBody body = new FormDataBody(Charsets.UTF_8, new FormDataBody.NameValuePair("name", "value"));

        assertThat(body.getContent()).hasSize(1);
        assertThat(body.getContent()[0].getName()).isEqualTo("name");
        assertThat(body.getContent()[0].getValue()).isEqualTo("value");
        assertThat(body.getContentType()).isEqualTo("application/x-www-form-urlencoded");
        assertThat(body.getCharset()).isEqualTo(Charsets.UTF_8);
    }
}
