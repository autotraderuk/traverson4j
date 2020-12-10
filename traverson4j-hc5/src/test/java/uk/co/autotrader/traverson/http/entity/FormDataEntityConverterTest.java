package uk.co.autotrader.traverson.http.entity;

import com.google.common.base.Charsets;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.FormDataBody;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FormDataEntityConverterTest {

    private FormDataEntityConverter converter;

    @Before
    public void setUp() {
        converter = new FormDataEntityConverter();
    }

    @Test
    public void toEntity_GivenTextBody_ReturnsStringEntity() throws Exception {

        HttpEntity entity = converter.toEntity(new FormDataBody(Charsets.UTF_8, new FormDataBody.NameValuePair("name", "value")));

        assertThat(entity).isInstanceOf(UrlEncodedFormEntity.class);
        assertThat(entity.getContent()).hasSameContentAs(new ByteArrayInputStream("name=value".getBytes(Charsets.UTF_8)));
        assertThat(entity.getContentType()).isEqualTo("application/x-www-form-urlencoded; charset=UTF-8");
    }
}
