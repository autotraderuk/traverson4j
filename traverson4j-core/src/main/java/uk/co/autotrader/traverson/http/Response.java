package uk.co.autotrader.traverson.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Response<T> {
    private int statusCode;
    private URI uri;
    private T resource;
    private String error;
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
        return resource;
    }

    public void setResource(T resource) {
        this.resource = resource;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public boolean isSuccessful() {
        return statusCode / 100 == 2;
    }

    public void addResponseHeader(String name, String value) {
        this.responseHeaders.put(name, value);
    }
}
