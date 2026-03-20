package uk.co.autotrader.traverson.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Response<T> {
    private int statusCode;
    private URI uri;
    private T resource;
    private boolean resourceConsumed = false;
    private Supplier<T> resourceF = () -> null;
    private Map<String, String> responseHeaders = new HashMap<String, String>();

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public T getResource() {
        if (!resourceConsumed) {
            resource = resourceF.get();
            resourceConsumed = true;
        }
        return resource;
    }

    public void setResource(T resource) {
        setResource(() -> resource);
    }

    public void setResource(Supplier<T> resourceF) {
        this.resourceConsumed = false;
        this.resourceF = resourceF;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public boolean isSuccessful() {
        return statusCodeFamily(2);
    }

    public boolean isFailure() {
        return is3xx() || is4xx() || is5xx();
    }

    public boolean is1xx() {
        return statusCodeFamily(1);
    }

    public boolean is3xx() {
        return statusCodeFamily(3);
    }

    public boolean is4xx() {
        return statusCodeFamily(4);
    }

    public boolean is5xx() {
        return statusCodeFamily(5);
    }

    private boolean statusCodeFamily(int family) {
        return statusCode / 100 == family;
    }

    public void addResponseHeader(String name, String value) {
        this.responseHeaders.put(name, value);
    }
}
