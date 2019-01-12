package uk.co.autotrader.traverson.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.InputStream;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConverterTest {
    private Converter converter;
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

    @Before
    public void setUp() throws Exception {
        converter = new Converter(bodyFactory, uriUtils, conversionService);
    }

    @Test
    public void toRequest_SetsHttpVerb() throws Exception {
        Request request = new Request();
        request.setMethod(Method.PATCH);

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getMethod()).isEqualTo("PATCH");
    }

    @Test
    public void toRequest_SetsUrl() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080";
        request.setUrl(url);
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn(url);

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getURI().toASCIIString()).isEqualTo(url);
    }

    @Test
    public void toRequest_AppendsQueryParams() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080";
        request.setUrl(url);
        request.addQueryParam("key1", "value1");
        request.addQueryParam("key2", "value2");
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn(url);

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getURI().toASCIIString()).isEqualTo("http://localhost:8080?key1=value1&key2=value2");
    }

    @Test
    public void toRequest_ExpandsTemplateUriBeforeBuildingRequest() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        String url = "http://localhost:8080/{tmp1}/stuff{?tmp2}";
        request.setUrl(url);
        request.addTemplateParam("tmp1", "abc");
        request.addTemplateParam("tmp2", "123");
        when(uriUtils.expandTemplateUri(url, request.getTemplateParams())).thenReturn("http://localhost:8080/abc/stuff?tmp2=123");

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getURI().toASCIIString()).isEqualTo("http://localhost:8080/abc/stuff?tmp2=123");
        verify(uriUtils).expandTemplateUri(url, request.getTemplateParams());
    }

    @Test
    public void toRequest_SetsHeaders() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.addHeader("header1", "value1");
        request.addHeader("header2", "value2");

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getFirstHeader("header1").getValue()).isEqualTo("value1");
        assertThat(uriRequest.getFirstHeader("header2").getValue()).isEqualTo("value2");
    }

    @Test
    public void toRequest_SetsAcceptHeader() throws Exception {
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setAcceptMimeType("application/json");

        HttpUriRequest uriRequest = converter.toRequest(request);

        assertThat(uriRequest.getFirstHeader("Accept").getValue()).isEqualTo("application/json");
    }

    @Test
    public void toRequest_SetsHttpEntity() throws Exception {
        Body<?> body = mock(Body.class);
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setBody(body);
        when(bodyFactory.toEntity(body)).thenReturn(httpEntity);

        HttpEntityEnclosingRequestBase uriRequest = (HttpEntityEnclosingRequestBase) converter.toRequest(request);

        assertThat(uriRequest.getEntity()).isEqualTo(httpEntity);
    }

    @Test
    public void toResponse_BuildsResponseCorrectly() throws Exception {
        URI requestUri = new URI("http://localhost");
        StatusLine statusLine = mock(StatusLine.class);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(httpResponse.getAllHeaders()).thenReturn(new Header[]{new BasicHeader("Location", "http://localhost/new")});

        Response<String> response = converter.toResponse(httpResponse, requestUri, String.class);

        assertThat(response).isNotNull();
        assertThat(response.getUri()).isEqualTo(requestUri);
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getResource()).isNull();
        assertThat(response.getResponseHeaders()).containsEntry("Location", "http://localhost/new");
    }

    @Test
    public void toResponse_GivenResponseHasEntity_ConvertsAndSetsResource() throws Exception {
        URI requestUri = new URI("http://localhost");
        String expectedJson = "{'name':'test'}";
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(httpResponse.getEntity()).thenReturn(httpEntity);
        when(httpEntity.getContent()).thenReturn(inputStream);
        when(conversionService.convert(inputStream, String.class)).thenReturn(expectedJson);
        StatusLine statusLine = mock(StatusLine.class);
        when(httpResponse.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(202);
        when(httpResponse.getAllHeaders()).thenReturn(new Header[0]);

        Response<String> response = converter.toResponse(httpResponse, requestUri, String.class);

        assertThat(response.getResource()).isEqualTo(expectedJson);
    }
}
