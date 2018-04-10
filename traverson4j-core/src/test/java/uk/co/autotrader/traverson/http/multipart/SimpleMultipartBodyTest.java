package uk.co.autotrader.traverson.http.multipart;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SimpleMultipartBodyTest {

    @Test
    public void getContentType_ReturnsNull() throws Exception {
        assertThat(new SimpleMultipartBody().getContentType()).isNull();
    }

    @Test
    public void init_GivenValues_SetsProperties() throws Exception {
        SimpleMultipartBody.BodyPart[] bodyParts = new SimpleMultipartBody.BodyPart[]{mock(SimpleMultipartBody.BodyPart.class), mock(SimpleMultipartBody.BodyPart.class)};

        SimpleMultipartBody body = new SimpleMultipartBody(bodyParts);

        assertThat(body.getContent()).isEqualTo(bodyParts);
    }
}
