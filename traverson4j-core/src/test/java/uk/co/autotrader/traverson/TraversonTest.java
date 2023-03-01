package uk.co.autotrader.traverson;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.Request;
import uk.co.autotrader.traverson.http.TraversonClient;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TraversonTest {

    @Mock
    private TraversonClient traversonClient;
    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Test
    void from_GivenUrl_ReturnsTraversonBuilder() throws Exception {
        Traverson traverson = new Traverson(traversonClient);

        TraversonBuilder builder = traverson.from("https://localhost:8080");

        assertThat(builder).isNotNull();
        Request request = (Request) FieldUtils.readDeclaredField(builder, "request", true);
        assertThat(request.getUrl()).isEqualTo("https://localhost:8080");
    }
}
