package uk.co.autotrader.traverson.exception;

import org.junit.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


public class IllegalHttpStatusExceptionTest {

    @Test
    public void init_SetsStatusAndUriFields() throws Exception {
        URI uri = URI.create("http://localhost");

        IllegalHttpStatusException exception = new IllegalHttpStatusException(404, uri);

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getUri()).isEqualTo(uri);
    }
}
