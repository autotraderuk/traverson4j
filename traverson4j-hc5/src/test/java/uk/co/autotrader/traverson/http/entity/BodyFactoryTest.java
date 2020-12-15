package uk.co.autotrader.traverson.http.entity;

import org.apache.hc.core5.http.HttpEntity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.http.Body;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BodyFactoryTest {
    private BodyFactory bodyFactory;
    @Mock
    private HttpEntityConverter converter;
    @Mock
    private Body body;
    @Mock
    private HttpEntity entity;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        bodyFactory = new BodyFactory();
        when(converter.toEntity(body)).thenReturn(entity);
    }

    @Test
    public void toEntity_GivenConverterIsRegistered_ReturnsConvertedResult() throws Exception {
        BodyFactory.register(body.getClass(), converter);
        assertThat(bodyFactory.toEntity(body)).isEqualTo(entity);
    }

    @Test
    public void toEntity_GivenConverterIsNotRegistered_ThrowsException() throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Not supported Request body, the supported types are ");

        assertThat(bodyFactory.toEntity(body)).isEqualTo(entity);
    }
}
