package uk.co.autotrader.traverson.http.entity;

import com.google.common.base.Charsets;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.TextBody;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TextEntityConverterTest {

    private TextEntityConverter converter;

    @Before
    public void setUp() {
        converter = new TextEntityConverter();
    }

    @Test
    public void toEntity_GivenTextBody_ReturnsStringEntity() throws Exception {
        HttpEntity entity = converter.toEntity(new TextBody("data££", "text/plain", Charsets.UTF_8));

        assertThat(entity).isInstanceOf(StringEntity.class);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream("data££".getBytes(Charsets.UTF_8)));
        assertThat(entity.getContentType()).isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    public void toEntity_GivenTextBody_WithNoCharsetDefined_ReturnsStringEntityUsingApacheDefault() throws Exception {
        HttpEntity entity = converter.toEntity(new TextBody("data££", "text/plain"));

        assertThat(entity).isInstanceOf(StringEntity.class);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream("data££".getBytes(Charsets.ISO_8859_1)));
        assertThat(entity.getContentType()).isEqualTo("text/plain");
    }
}
