package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.http.Body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BodyFactoryTest {
    private BodyFactory bodyFactory;
    @Mock
    private HttpEntityConverter converter;
    @Mock
    private Body body;
    @Mock
    private HttpEntity entity;

    @BeforeEach
    void setUp() {
        bodyFactory = new BodyFactory();
    }

    @Test
    void toEntity_GivenConverterIsRegistered_ReturnsConvertedResult() {
        when(converter.toEntity(body)).thenReturn(entity);
        BodyFactory.register(body.getClass(), converter);
        assertThat(bodyFactory.toEntity(body)).isEqualTo(entity);
    }

    @Test
    void toEntity_GivenConverterIsNotRegistered_ThrowsException() {
        UnsupportedOperationException exception = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            HttpEntity newEntity = bodyFactory.toEntity(body);
            assertThat(newEntity).isEqualTo(entity);
        });

        assertThat(exception.getMessage())
                .contains("Not supported Request body, the supported types are")
                .contains("class uk.co.autotrader.traverson.http.SimpleMultipartBody")
                .contains("class uk.co.autotrader.traverson.http.TextBody")
                .contains("class uk.co.autotrader.traverson.http.FormDataBody");
    }
}
