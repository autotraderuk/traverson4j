package uk.co.autotrader.traverson.http;

import java.util.*;
import java.util.function.BiConsumer;

public class Request {

    private String url;
    private Method method;
    private String acceptMimeType;
    private final Map<String, String> headers;
    private final Map<String, List<String>> queryParameters;
    private final Map<String, List<String>> templateParams;
    private Body body;
    private final List<AuthCredential> authCredentials;

    public Request() {
        queryParameters = new HashMap<>();
        templateParams = new HashMap<>();
        headers = new LinkedHashMap<>();
        authCredentials = new LinkedList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public List<AuthCredential> getAuthCredentials() {
        return authCredentials;
    }

    public String getAcceptMimeType() {
        return acceptMimeType;
    }

    public void setAcceptMimeType(String acceptMimeType) {
        this.acceptMimeType = acceptMimeType;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void addAuthCredential(AuthCredential authCredential) {
        this.authCredentials.add(authCredential);
    }

    public Map<String, List<String>> getQueryParameters() {
        return queryParameters;
    }

    public void addQueryParam(String name, String... values) {
        addParameters(this.queryParameters).accept(name, values);
    }

    public Map<String, List<String>> getTemplateParams() {
        return templateParams;
    }

    public void addTemplateParam(String name, String... values) {
        addParameters(this.templateParams).accept(name, values);
    }

    private BiConsumer<String, String[]> addParameters(Map<String, List<String>> parameterMap) {
        return (name, values) -> {
            if (!parameterMap.containsKey(name)) {
                parameterMap.put(name, new LinkedList<>());
            }
            parameterMap.get(name).addAll(Arrays.asList(values));
        };
    }
}
