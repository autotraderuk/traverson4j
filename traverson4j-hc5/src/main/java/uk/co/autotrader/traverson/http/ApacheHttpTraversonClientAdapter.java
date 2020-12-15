package uk.co.autotrader.traverson.http;

import com.alibaba.fastjson.util.IOUtils;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import uk.co.autotrader.traverson.conversion.ResourceConversionService;
import uk.co.autotrader.traverson.exception.HttpException;
import uk.co.autotrader.traverson.http.entity.BodyFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class ApacheHttpTraversonClientAdapter implements TraversonClient {

    private final CloseableHttpClient adapterClient;
    private final ApacheHttpUriConverter apacheHttpUriConverter;


    public ApacheHttpTraversonClientAdapter() {
        this(HttpClients.createDefault());
    }

    public ApacheHttpTraversonClientAdapter(CloseableHttpClient client) {
        this.adapterClient = client;
        this.apacheHttpUriConverter = new ApacheHttpUriConverter(new BodyFactory(), new TemplateUriUtils(), ResourceConversionService.getInstance());
    }

    @Override
    public <T> Response<T> execute(Request request, Class<T> returnType) {
        ClassicHttpRequest httpRequest = apacheHttpUriConverter.toRequest(request);

        HttpClientContext clientContext = HttpClientContext.create();
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthCache authCache = new BasicAuthCache();

        setUserCredentialsAndAuthCache(request.getAuthCredentials(), credentialsProvider, authCache);

        clientContext.setCredentialsProvider(credentialsProvider);
        clientContext.setAuthCache(authCache);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = adapterClient.execute(httpRequest, clientContext);
            return apacheHttpUriConverter.toResponse(httpResponse, httpRequest.getUri(), returnType);
        } catch (IOException | URISyntaxException e) {
            throw new HttpException("Error with httpClient", e);
        } finally {
            IOUtils.close(httpResponse);
        }
    }

    private void setUserCredentialsAndAuthCache(List<AuthCredential> authCredentialList, BasicCredentialsProvider credentialsProvider, AuthCache authCache) {
        for (AuthCredential authCredential : authCredentialList) {
            UsernamePasswordCredentials userPassword = new UsernamePasswordCredentials(authCredential.getUsername(), authCredential.getPassword().toCharArray());
            AuthScope authScope = new AuthScope(null, null, -1, null, null);
            if (authCredential.getHostname() != null) {
                HttpHost target;
                try {
                    target = HttpHost.create(authCredential.getHostname());
                    authScope = new AuthScope(target);

                } catch (URISyntaxException e) {
                    throw new HttpException("Error with HttpHost", e);
                }

                if (authCredential.isPreemptiveAuthentication()) {
                    BasicScheme authScheme = new BasicScheme();
                    authScheme.initPreemptive(userPassword);
                    authCache.put(target, authScheme);
                }
            }

            credentialsProvider.setCredentials(authScope, userPassword);
        }
    }
}
