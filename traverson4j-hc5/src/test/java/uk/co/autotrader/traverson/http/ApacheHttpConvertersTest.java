package uk.co.autotrader.traverson.http;

import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.message.BasicHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApacheHttpConvertersTest {
    private ApacheHttpConverters apacheHttpUriConverter;
    @Mock
    private BodyFactory bodyFactory;
    @Mock
    private TemplateUriUtils uriUtils;
    @Mock
    private ResourceConversionService conversionService;
    @Mock
    private HttpEntity httpEntity;
    @Mock
    private CloseableHttpResponse httpResponse;
    @Mock
    private AuthCredential authCredential;

    @BeforeEach
    void setUp() {
        apacheHttpUriConverter = new ApacheHttpConverters(bodyFactory, uriUtils, conversionService);
    }

    @Test
    void toRequest_SetsHttpVerb() {
        Request request = new Request();
        request.setMethod(Method.PATCH);

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getMethod()).isEqualTo("PATCH");
    }

    @Test
    void toRequest_SetsUrl() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080/";
        request.setUrl(url);
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn(url);

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getUri().toASCIIString()).isEqualTo(url);
    }

    @Test
    void toRequest_AppendsQueryParams() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080";
        request.setUrl(url);
        request.addQueryParam("key1", "value1");
        request.addQueryParam("key2", "value2");
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn(url);

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getUri().toASCIIString()).isEqualTo("http://localhost:8080/?key1=value1&key2=value2");
    }

    @Test
    void toRequest_ExpandsTemplateUriBeforeBuildingRequest() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080/{tmp1}/stuff{?tmp2}";
        request.setUrl(url);
        request.addTemplateParam("tmp1", "abc");
        request.addTemplateParam("tmp2", "123");
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn("http://localhost:8080/abc/stuff?tmp2=123");

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getUri().toASCIIString()).isEqualTo("http://localhost:8080/abc/stuff?tmp2=123");
        verify(uriUtils).expandTemplateUri(url, request.getTemplateParams());
    }

    @Test
    void toRequest_SetsHeaders() {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.addHeader("header1", "value1");
        request.addHeader("header2", "value2");

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getFirstHeader("header1").getValue()).isEqualTo("value1");
        assertThat(uriRequest.getFirstHeader("header2").getValue()).isEqualTo("value2");
    }

    @Test
    void toRequest_SetsAcceptHeader() {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setAcceptMimeType("application/json");

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
    }

    @Test
    void toRequest_SetsHttpEntity() {
        Body<?> body = mock(Body.class);
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setBody(body);
        when(bodyFactory.toEntity(body)).thenReturn(httpEntity);

        ClassicHttpRequest uriRequest = apacheHttpUriConverter.toRequest(request);

        assertThat(uriRequest.getEntity()).isEqualTo(httpEntity);
    }

    @Test
    void toResponse_BuildsResponseCorrectly() throws Exception {
        HttpRequest request =  mock(HttpRequest.class);
        URI requestUri = new URI("http://localhost");

        when(request.getUri()).thenReturn(requestUri);
        when(httpResponse.getCode()).thenReturn(200);
        when(httpResponse.getHeaders()).thenReturn(new Header[]{new BasicHeader("Location", "http://localhost/new")});


        Response<String> response = apacheHttpUriConverter.toResponse(httpResponse, String.class, request.getUri());

        assertThat(response).isNotNull();
        assertThat(response.getUri()).isEqualTo(requestUri);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResource()).isNull();
        assertThat(response.getResponseHeaders()).containsEntry("Location", "http://localhost/new");
    }

    @Test
    void toResponse_GivenResponseHasEntity_ConvertsAndSetsResource() throws Exception {
        HttpRequest request =  mock(HttpRequest.class);
        URI requestUri = new URI("http://localhost");
        String expectedJson = "{'name':'test'}";
        InputStream inputStream = Mockito.mock(InputStream.class);

        when(request.getUri()).thenReturn(requestUri);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(conversionService.convert(inputStream, String.class)).thenReturn(expectedJson);
        when(httpResponse.getCode()).thenReturn(202);
        when(httpResponse.getHeaders()).thenReturn(new Header[0]);

        Response<String> response = apacheHttpUriConverter.toResponse(httpResponse, String.class, request.getUri());

        assertThat(response.getResource()).isEqualTo(expectedJson);
    }

    @Test
    void constructCredentialsProviderAndAuthCache_ifNoHostnameReturnsAnyAuthScope() {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
        AuthCache authCache = new BasicAuthCache();

        when(authCredential.getUsername()).thenReturn("username");
        when(authCredential.getPassword()).thenReturn("password");

        apacheHttpTraversonClientAdapter.apacheHttpUriConverter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);

        assertThat(basicCredentialsProvider).hasToString("[<any auth scheme> <any realm> <any protocol>://<any host>:<any port>]");
        assertThat(authCache.get(new HttpHost("hostname"))).isNull();
    }

    @Test
    void constructCredentialsProviderAndAuthCache_setsHostnameInAuthScope() {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
        AuthCache authCache = new BasicAuthCache();

        when(authCredential.getUsername()).thenReturn("username");
        when(authCredential.getPassword()).thenReturn("password");
        when(authCredential.getHostname()).thenReturn("hostname");

        apacheHttpTraversonClientAdapter.apacheHttpUriConverter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);

        assertThat(basicCredentialsProvider).hasToString("[<any auth scheme> <any realm> http://hostname:<any port>]");
        assertThat(authCache.get(new HttpHost("hostname"))).isNull();
    }

    @Test
    void constructCredentialsProviderAndAuthCache_setsHostnameAndBasicAuth() {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        AuthCache authCache = new BasicAuthCache();
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();

        when(authCredential.getUsername()).thenReturn("username");
        when(authCredential.getPassword()).thenReturn("password");
        when(authCredential.getHostname()).thenReturn("hostname");
        when(authCredential.isPreemptiveAuthentication()).thenReturn(true);

        apacheHttpTraversonClientAdapter.apacheHttpUriConverter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential);

        assertThat(basicCredentialsProvider).hasToString("[<any auth scheme> <any realm> http://hostname:<any port>]");
        assertThat(authCache.get(new HttpHost("hostname")).getName()).isEqualTo("Basic");
    }

    @Test
    void constructCredentialsProviderAndAuthCache_throwsExceptionWhenHostnameContainsSpaces() {
        BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
        ApacheHttpTraversonClientAdapter apacheHttpTraversonClientAdapter = new ApacheHttpTraversonClientAdapter();
        AuthCache authCache = new BasicAuthCache();

        when(authCredential.getHostname()).thenReturn("hostname with spaces");
        when(authCredential.getUsername()).thenReturn("username");
        when(authCredential.getPassword()).thenReturn("password");

        assertThatThrownBy(() -> apacheHttpTraversonClientAdapter.apacheHttpUriConverter.constructCredentialsProviderAndAuthCache(basicCredentialsProvider, authCache, authCredential))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Preemptive authentication hostname is invalid")
                .hasCauseInstanceOf(URISyntaxException.class);
    }
}
