package uk.co.autotrader.traverson.exception;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


class IllegalHttpStatusExceptionTest {

    @Test
    void init_SetsStatusAndUriFields() {
        URI uri = URI.create("http://localhost");

        IllegalHttpStatusException exception = new IllegalHttpStatusException(404, uri);

        assertThat(exception.getStatusCode()).isEqualTo(404);
        assertThat(exception.getUri()).isEqualTo(uri);
    }
}
