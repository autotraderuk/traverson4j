package uk.co.autotrader.traverson.http.multipart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.SimpleMultipartBody;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class SimpleMultipartBodyTest {

    @Test
    void getContentType_ReturnsNull() {
        assertThat(new SimpleMultipartBody().getContentType()).isNull();
    }

    @Test
    void init_GivenValues_SetsProperties() {
        SimpleMultipartBody.BodyPart[] bodyParts = new SimpleMultipartBody.BodyPart[]{mock(SimpleMultipartBody.BodyPart.class), mock(SimpleMultipartBody.BodyPart.class)};

        SimpleMultipartBody body = new SimpleMultipartBody(bodyParts);

        assertThat(body.getContent()).isEqualTo(bodyParts);
    }
}
