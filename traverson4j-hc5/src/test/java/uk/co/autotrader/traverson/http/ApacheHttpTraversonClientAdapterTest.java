package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.autotrader.traverson.exception.HttpException;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApacheHttpTraversonClientAdapterTest {
    private ApacheHttpTraversonClientAdapter clientAdapter;
    @Mock
    private CloseableHttpClient httpClient;
    @Mock
    private ApacheHttpConverters apacheHttpConverters;
    @Mock
    private HttpUriRequest httpRequest;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private HttpClientContext clientContext;

    private Request request;
    private Response<JSONObject> expectedResponse;

    @Before
    public void setUp() throws Exception {
        clientAdapter = new ApacheHttpTraversonClientAdapter(httpClient);
        FieldUtils.writeField(clientAdapter, "apacheHttpUriConverter", apacheHttpConverters, true);
        request = new Request();
        expectedResponse = new Response<>();
        when(apacheHttpConverters.toRequest(request)).thenReturn(httpRequest);
        when(apacheHttpConverters.toResponse(httpResponse, JSONObject.class, httpRequest.getUri())).thenReturn(expectedResponse);
        when(apacheHttpConverters.toHttpClientContext(request)).thenReturn(clientContext);
    }

    @Test
    public void execute_GivenGetRequest_ReturnsResponse() throws Exception {
        when(httpClient.execute(httpRequest, clientContext)).thenReturn(httpResponse);

        Response<JSONObject> response = clientAdapter.execute(request, JSONObject.class);

        assertThat(response).isEqualTo(expectedResponse);
        verify(httpResponse).close();
    }

    @Test
    public void execute_GivenGetRequestForInputStream_ReturnsResponseButDoesntCloseTheResponse() throws Exception {
        when(httpClient.execute(httpRequest, clientContext)).thenReturn(httpResponse);
        Response<InputStream> expectedStreamResponse = new Response<>();
        when(apacheHttpConverters.toResponse(httpResponse, InputStream.class, httpRequest.getUri())).thenReturn(expectedStreamResponse);

        Response<InputStream> response = clientAdapter.execute(request, InputStream.class);

        assertThat(response).isSameAs(expectedStreamResponse);
        verify(httpResponse, never()).close();
    }

    @Test
    public void execute_GivenIOExceptionIsThrown_WrapsInTraversonException() throws Exception {
        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);
        when(apacheHttpConverters.toResponse(httpResponse, JSONObject.class, httpRequest.getUri())).thenThrow(new IOException());

        assertThatThrownBy(() -> clientAdapter.execute(request, JSONObject.class)).isInstanceOf(HttpException.class);

        verify(httpResponse).close();
    }

    @Test
    public void execute_GivenRuntimeExceptionIsThrown_ClosesInputStreamAndReturnsOriginalException() throws Exception {
        NullPointerException nullPointerException = new NullPointerException();
        when(httpClient.execute(eq(httpRequest), any(HttpClientContext.class))).thenReturn(httpResponse);
        when(apacheHttpConverters.toResponse(httpResponse, JSONObject.class, httpRequest.getUri())).thenThrow(nullPointerException);

        assertThatThrownBy(() -> clientAdapter.execute(request, JSONObject.class)).isSameAs(nullPointerException);

        verify(httpResponse).close();
    }

    @Test
    public void init_SetsDefaultHttpClient() throws Exception {
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();

        assertThat(FieldUtils.readField(apacheHttpTraversonClientAdapter, "adapterClient", true)).isNotNull();
    }

}
