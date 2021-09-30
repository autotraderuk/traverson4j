package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.exception.HttpException;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class ApacheHttpTraversonClientAdapter implements TraversonClient {

    private final CloseableHttpClient adapterClient;
    final ApacheHttpConverters apacheHttpUriConverter;


    public ApacheHttpTraversonClientAdapter() {
        this(HttpClients.createDefault());
    }

    public ApacheHttpTraversonClientAdapter(CloseableHttpClient client) {
        this.adapterClient = client;
        this.apacheHttpUriConverter = new ApacheHttpConverters(new BodyFactory(), new TemplateUriUtils(), ResourceConversionService.getInstance());
    }

    @Override
    public <T> Response<T> execute(Request request, Class<T> returnType) {
        ClassicHttpRequest httpRequest = apacheHttpUriConverter.toRequest(request);
        HttpClientContext clientContext = apacheHttpUriConverter.toHttpClientContext(request);
        CloseableHttpResponse httpResponse = null;
        boolean shouldCloseStream = !returnType.isAssignableFrom(InputStream.class);
        try {
            httpResponse = adapterClient.execute(httpRequest, clientContext);
            return apacheHttpUriConverter.toResponse(httpResponse, returnType, httpRequest.getUri());
        } catch (RuntimeException runtimeException) {
            shouldCloseStream = true;
            throw runtimeException;
        } catch (IOException | URISyntaxException e) {
            shouldCloseStream = true;
            throw new HttpException("Error with httpClient", e);
        } finally {
            if (shouldCloseStream) {
                IOUtils.close(httpResponse);
            }
        }
    }
}
