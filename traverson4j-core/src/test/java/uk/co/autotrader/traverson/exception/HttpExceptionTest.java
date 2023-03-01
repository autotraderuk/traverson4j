package uk.co.autotrader.traverson.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


class HttpExceptionTest {
    @Test
    void init_OverridesMessageAndCauseConstructor() {
        IOException cause = new IOException();

        HttpException exception = new HttpException("My Message", cause);

        assertThat(exception).hasMessage("My Message")
                .hasCauseExactlyInstanceOf(IOException.class);
    }
}
