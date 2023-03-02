package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ResponseTest {

    @Mock
    private Map<String, String> headers;

    @Test
    void pojo() {
        URI uri = URI.create("http://localhost");
        JSONObject resource = new JSONObject();
        Response<JSONObject> response = new Response<>();
        response.setResource(resource);
        response.setStatusCode(200);
        response.setUri(uri);
        response.setResponseHeaders(headers);

        assertThat(response.getResource()).isEqualTo(resource);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getUri()).isEqualTo(uri);
        assertThat(response.getResponseHeaders()).isEqualTo(headers);
    }

    @ParameterizedTest(name = "given{0}StatusCode_isSuccessfulReturnsFalse")
    @ValueSource(ints = {500, 404, 302})
    void isSuccessful_GivenUnsuccessfulStatusCode_ReturnsFalse(int statusCode) {
        Response response = new Response();
        response.setStatusCode(statusCode);

        assertThat(response.isSuccessful()).isFalse();
    }

    @Test
    void isSuccessful_Given201StatusCode_ReturnsTrue() {
        Response response = new Response();
        response.setStatusCode(201);

        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    void addResponseHeader_AppendsTheUniqueHeaders() {
        Response<?> response = new Response();
        response.addResponseHeader("key1", "value1");
        response.addResponseHeader("key2", "value2");
        response.addResponseHeader("key2", "value3");

        assertThat(response.getResponseHeaders()).containsEntry("key1", "value1").containsEntry("key2", "value3");

    }
}
