package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.HttpException;

import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApacheHttpTraversonClientAdapterTest {
    private ApacheHttpTraversonClientAdapter clientAdapter;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private Converter converter;
    @Mock
    private HttpUriRequest httpRequest;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Captor
    private ArgumentCaptor<HttpClientContext> clientContextCaptor;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private Request request;
    private Response<JSONObject> expectedResponse;

    @Before
    public void setUp() throws Exception {
        URI uri = new URI("http://localhost");
        clientAdapter = new ApacheHttpTraversonClientAdapter(httpClient);
        FieldUtils.writeField(clientAdapter, "converter", converter, true);
        request = new Request();
        expectedResponse = new Response<JSONObject>();
        when(httpRequest.getURI()).thenReturn(uri);
        when(converter.toRequest(request)).thenReturn(httpRequest);
        when(converter.toResponse(httpResponse, uri, JSONObject.class)).thenReturn(expectedResponse);
    }

    @Test
    public void execute_GivenGetRequest_ReturnsResponse() throws Exception {
        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);

        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);

        assertThat(response).isEqualTo(expectedResponse);
        verify(httpResponse).close();
    }
    @Test
    public void execute_GivenIOExceptionIsThrown_WrapsInTraversonException() throws Exception {
        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);
        when(converter.toResponse(httpResponse, httpRequest.getURI(), JSONObject.class)).thenThrow(new IOException());
        expectedException.expect(HttpException.class);

        clientAdapter.execute(request, JSONObject.class);

        verify(httpResponse).close();
    }

    @Test
    public void execute_GivenRequestWithAuthCredentials() throws Exception {
        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
        request.addAuthCredential(new AuthCredential("user", "password", null));

        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);

        assertThat(response).isEqualTo(expectedResponse);
        HttpClientContext clientContext = clientContextCaptor.getValue();
        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(AuthScope.ANY);
        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("password");
        verify(httpResponse).close();
    }

    @Test
    public void execute_GivenRequestWithScopedAuthCredentials() throws Exception {
        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
        request.addAuthCredential(new AuthCredential("user", "password", "myhost.autotrader.co.uk"));

        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);

        assertThat(response).isEqualTo(expectedResponse);
        HttpClientContext clientContext = clientContextCaptor.getValue();
        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(new AuthScope(new HttpHost("myhost.autotrader.co.uk")));
        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user");
        assertThat(credentials.getPassword()).isEqualTo("password");
        verify(httpResponse).close();
    }

    @Test
    public void execute_GivenMultipleAuthCredentials() throws Exception {
        when(httpClient.execute(eq(httpRequest), clientContextCaptor.capture())).thenReturn(httpResponse);
        request.addAuthCredential(new AuthCredential("user", "password", "myhost.autotrader.co.uk"));
        request.addAuthCredential(new AuthCredential("user2", "password2", "myhost.autotrader.co.uk"));
        request.addAuthCredential(new AuthCredential("user3", "password3", null));

        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);

        assertThat(response).isEqualTo(expectedResponse);
        HttpClientContext clientContext = clientContextCaptor.getValue();
        Credentials credentials = clientContext.getCredentialsProvider().getCredentials(new AuthScope(new HttpHost("myhost.autotrader.co.uk")));
        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user2");
        assertThat(credentials.getPassword()).isEqualTo("password2");
        credentials = clientContext.getCredentialsProvider().getCredentials(AuthScope.ANY);
        assertThat(credentials.getUserPrincipal().getName()).isEqualTo("user3");
        assertThat(credentials.getPassword()).isEqualTo("password3");
        verify(httpResponse).close();
    }

    @Test
    public void init_SetsDefaultHttpClient() throws Exception {
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();

        assertThat(FieldUtils.readField(apacheHttpTraversonClientAdapter, "adapterClient", true)).isNotNull();
    }
}
