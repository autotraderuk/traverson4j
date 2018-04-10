package uk.co.autotrader.traverson.http;

import com.google.common.base.Charsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TextBodyTest {

    @Test
    public void init_GivenValues_SetsProperties() throws Exception {
        TextBody body = new TextBody("data", "contentType", Charsets.UTF_8);

        assertThat(body.getContent()).isEqualTo("data");
        assertThat(body.getContentType()).isEqualTo("contentType");
        assertThat(body.getCharset()).isEqualTo(Charsets.UTF_8);
    }

    @Test
    public void legacyinit_GivenValues_SetsProperties() throws Exception {
        TextBody body = new TextBody("data", "contentType");

        assertThat(body.getContent()).isEqualTo("data");
        assertThat(body.getContentType()).isEqualTo("contentType");
        assertThat(body.getCharset()).isNull();
    }
}