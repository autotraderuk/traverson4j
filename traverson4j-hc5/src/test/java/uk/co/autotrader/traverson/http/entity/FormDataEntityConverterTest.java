package uk.co.autotrader.traverson.http.entity;

import com.google.common.base.Charsets;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.FormDataBody;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FormDataEntityConverterTest {

    private FormDataEntityConverter converter;

    @BeforeEach
    void setUp() {
        converter = new FormDataEntityConverter();
    }

    @Test
    void toEntity_GivenTextBody_ReturnsStringEntity() throws Exception {

        HttpEntity entity = converter.toEntity(new FormDataBody(Charsets.UTF_8, new FormDataBody.NameValuePair("name", "value")));

        assertThat(entity).isInstanceOf(UrlEncodedFormEntity.class);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream("name=value".getBytes(Charsets.UTF_8)));
        assertThat(entity.getContentType()).isEqualTo("application/x-www-form-urlencoded; charset=UTF-8");
    }
}
