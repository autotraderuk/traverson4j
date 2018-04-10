package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.datatype.AuthCredential;
import uk.co.autotrader.traverson.exception.HttpException;
import uk.co.autotrader.traverson.http.entity.BodyFactory;
import uk.co.autotrader.traverson.link.TemplateUriUtils;

import java.io.IOException;

public class ApacheHttpTraversonClientAdapter implements TraversonClient {

    private final CloseableHttpClient adapterClient;
    private final Converter converter;


    public ApacheHttpTraversonClientAdapter() {
        this(HttpClients.createDefault());
    }

    public ApacheHttpTraversonClientAdapter(CloseableHttpClient client) {
        this.adapterClient = client;
        this.converter = new Converter(new BodyFactory(), new TemplateUriUtils(), ResourceConversionService.getInstance());
    }

    @Override
    public <T> Response<T> execute(Request request, Class<T> returnType) {
        HttpUriRequest httpUriRequest = converter.toRequest(request);

        HttpClientContext clientContext = HttpClientContext.create();
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        for (AuthCredential authCredential : request.getAuthCredentials()) {
            UsernamePasswordCredentials userPassword = new UsernamePasswordCredentials(authCredential.getUsername(), authCredential.getPassword());
            AuthScope scope = AuthScope.ANY;
            if (authCredential.getHostname() != null) {
               scope = new AuthScope(HttpHost.create(authCredential.getHostname()));
            }
            credentialsProvider.setCredentials(scope, userPassword);
        }

        clientContext.setCredentialsProvider(credentialsProvider);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = adapterClient.execute(httpUriRequest, clientContext);
            return converter.toResponse(httpResponse, httpUriRequest.getURI(), returnType);
        } catch (IOException e) {
            throw new HttpException("Error with httpClient", e);
        } finally {
            IOUtils.close(httpResponse);
        }
    }
}
