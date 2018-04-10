package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ResponseTest {

    @Mock
    private Map<String, String> headers;

    @Test
    public void pojo() throws Exception {
        URI uri = URI.create("http://localhost");
        JSONObject resource = new JSONObject();
        Response<JSONObject> response = new Response<JSONObject>();
        response.setResource(resource);
        response.setStatusCode(200);
        response.setUri(uri);
        response.setResponseHeaders(headers);

        assertThat(response.getResource()).isEqualTo(resource);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getUri()).isEqualTo(uri);
        assertThat(response.getResponseHeaders()).isEqualTo(headers);
    }

    @Test
    public void isSuccessful_Given500StatusCode_ReturnsFalse() throws Exception {
        Response response = new Response();
        response.setStatusCode(500);

        assertThat(response.isSuccessful()).isFalse();
    }

    @Test
    public void isSuccessful_Given404StatusCode_ReturnsFalse() throws Exception {
        Response response = new Response();
        response.setStatusCode(404);

        assertThat(response.isSuccessful()).isFalse();
    }

    @Test
    public void isSuccessful_Given302StatusCode_ReturnsFalse() throws Exception {
        Response response = new Response();
        response.setStatusCode(302);

        assertThat(response.isSuccessful()).isFalse();
    }

    @Test
    public void isSuccessful_Given201StatusCode_ReturnsTrue() throws Exception {
        Response response = new Response();
        response.setStatusCode(201);

        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void addResponseHeader_AppendsTheUniqueHeaders() throws Exception {
        Response<?> response = new Response();
        response.addResponseHeader("key1", "value1");
        response.addResponseHeader("key2", "value2");
        response.addResponseHeader("key2", "value3");

        assertThat(response.getResponseHeaders()).containsEntry("key1", "value1").containsEntry("key2", "value3");

    }
}
