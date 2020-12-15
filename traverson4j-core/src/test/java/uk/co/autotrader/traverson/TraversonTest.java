package uk.co.autotrader.traverson;


import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.Request;
import uk.co.autotrader.traverson.http.TraversonClient;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TraversonTest {

    @Mock
    private TraversonClient traversonClient;
    @Captor
    private ArgumentCaptor<Request> requestCaptor;

    @Test
    public void from_GivenUrl_ReturnsTraversonBuilder() throws Exception {
        Traverson traverson = new Traverson(traversonClient);

        TraversonBuilder builder = traverson.from("https://localhost:8080");

        assertThat(builder).isNotNull();
        Request request = (Request) FieldUtils.readDeclaredField(builder, "request", true);
        assertThat(request.getUrl()).isEqualTo("https://localhost:8080");
    }
}
