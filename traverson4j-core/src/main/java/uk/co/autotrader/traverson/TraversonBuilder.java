package uk.co.autotrader.traverson;

import com.alibaba.fastjson.JSONObject;
import uk.co.autotrader.traverson.http.AuthCredential;
import uk.co.autotrader.traverson.exception.IllegalHttpStatusException;
import uk.co.autotrader.traverson.http.*;
import uk.co.autotrader.traverson.link.BasicLinkDiscoverer;
import uk.co.autotrader.traverson.link.HalLinkDiscoverer;
import uk.co.autotrader.traverson.link.LinkDiscoverer;

import java.util.*;

/**
 * Not thread safe
 *
 * <p>A builder which constructs a specification for interacting with a REST API
 * conforming to <a href="https://tools.ietf.org/html/draft-kelly-json-hal-03">
 * JSON HAL standards</a>
 *
 * @author Michael Rocke
 */
public class TraversonBuilder {
    private TraversonClient traversonClient;
    private LinkDiscoverer linkDiscoverer;
    private Deque<String> relsToFollow;
    private Request request;

    TraversonBuilder(TraversonClient traversonClient) {
        this.traversonClient = traversonClient;
        relsToFollow = new LinkedList<>();
        request = new Request();
    }

    public TraversonBuilder from(String startingUrl) {
        request.setUrl(startingUrl);
        return this;
    }

    public TraversonBuilder json() {
        this.request.setAcceptMimeType("application/json");
        this.linkDiscoverer = new BasicLinkDiscoverer();
        return this;
    }

    public TraversonBuilder jsonHal() {
        this.request.setAcceptMimeType("application/hal+json");
        this.linkDiscoverer = new HalLinkDiscoverer();
        return this;
    }

    public TraversonBuilder follow(String... rels) {
        this.relsToFollow.clear();
        this.relsToFollow.addAll(Arrays.asList(rels));
        return this;
    }

    /**
     * A builder method for adding query parameters to the web request. This
     * method is additive and does not overwrite query param key/values already
     * passed to the builder.
     *
     * @param name the query parameter key
     * @param values a var args of values to associate with the key
     * @return the current builder inclusive of new values for query parameters
     */
    public TraversonBuilder withQueryParam(String name, String... values) {
        this.request.addQueryParam(name, values);
        return this;
    }

    /**
     * A builder method for adding template parameters to the web request. This
     * method is additive and does not overwrite template param key/values already
     * passed to the builder.
     *
     * @param name the template parameter key
     * @param values a var args of values to associate with the key
     * @return the current builder inclusive of new values for template parameters
     */
    public TraversonBuilder withTemplateParam(String name, String... values) {
        this.request.addTemplateParam(name, values);
        return this;
    }

    /**
     * A builder method for adding headers to the web request. This
     * method is not additive and will overwrite header values with the
     * same key.
     *
     * @param name the name of the header key
     * @param value the value to associate with the key
     * @return the current builder inclusive of the header requested
     */
    public TraversonBuilder withHeader(String name, String value) {
        this.request.addHeader(name, value);
        return this;
    }

    /**
     * Apply the following basic auth credentials on all http requests
     * @param username
     * @param password
     */
    public TraversonBuilder withAuth(String username, String password) {
        return withAuth(username, password, null);
    }

    /**
     * Apply the following basic auth credentials for only http requests on the supplied hostname
     * @param username
     * @param password
     * @param hostname simple definition of a hostname, e.g. "myservice.autotrader.co.uk"
     * @return
     */
    public TraversonBuilder withAuth(String username, String password, String hostname) {
        this.request.addAuthCredential(new AuthCredential(username, password, hostname));
        return this;
    }

    /**
     * Navigate the path and get the resource
     *
     * @return JSONObject representing end resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public JSONObject getResource() {
        return get().getResource();
    }

    /**
     * Navigate the path and get the response
     *
     * @return Response representing the http response and resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public Response<JSONObject> get() {
        return get(JSONObject.class);
    }

    /**
     * Navigate the path and get the response
     *
     * @param returnType what you want the resource to represented as
     * @return Response representing the http response and resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> Response<T> get(Class<T> returnType) {
        return traverseAndPerform(Method.GET, null, returnType);
    }

    /**
     * Navigate the path and delete the resource
     *
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public Response<JSONObject> delete() {
        return traverseAndPerform(Method.DELETE, null, JSONObject.class);
    }

    /**
     * Navigate the path and delete the resource
     *
     * @param returnType what you want the resource to represented as
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> Response<T> delete(Class<T> returnType) {
        return traverseAndPerform(Method.DELETE, null, returnType);
    }

    /**
     * Navigate the path and post the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public Response<JSONObject> post(Body body) {
        return traverseAndPerform(Method.POST, body, JSONObject.class);
    }

    /**
     * Navigate the path and post the body to the resource
     *
     * @param body       request body to send
     * @param returnType what you want the resource to represented as
     * @return Response representing the http response and resource
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> Response<T> post(Body body, Class<T> returnType) {
        return traverseAndPerform(Method.POST, body, returnType);
    }

    /**
     * Navigate the path and put the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public Response<JSONObject> put(Body body) {
        return traverseAndPerform(Method.PUT, body, JSONObject.class);
    }


    /**
     * Navigate the path and put the body to the resource
     *
     * @param body       request body to send
     * @param returnType what you want the resource to represented as
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> Response<T> put(Body body, Class<T> returnType) {
        return traverseAndPerform(Method.PUT, body, returnType);
    }

    /**
     * Navigate the path and patch the body to the resource
     *
     * @param body request body to send
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public Response<JSONObject> patch(Body body) {
        return traverseAndPerform(Method.PATCH, body, JSONObject.class);
    }

    /**
     * Navigate the path and patch the body to the resource
     *
     * @param body       request body to send
     * @param returnType what you want the resource to represented as
     * @return Response representing the http response
     * @throws uk.co.autotrader.traverson.exception.UnknownRelException          When navigating a path, a given rel cannot be found
     * @throws uk.co.autotrader.traverson.exception.IllegalHttpStatusException When a non 2xx response is returned part way through traversing
     * @throws uk.co.autotrader.traverson.exception.HttpException When the underlying http client experiences an issue with a request. This could be an intermittent issue
     */
    public <T> Response<T> patch(Body body, Class<T> returnType) {
        return traverseAndPerform(Method.PATCH, body, returnType);
    }

    private <T> Response<T> traverseAndPerform(Method terminalMethod, Body terminalBody, Class<T> returnType) {
        while (!relsToFollow.isEmpty()) {
            request.setMethod(Method.GET);
            Response<JSONObject> response = traversonClient.execute(request, JSONObject.class);
            if (response.isSuccessful()) {
                request.setUrl(linkDiscoverer.findHref(response.getResource(), relsToFollow.removeFirst()));
            } else {
                throw new IllegalHttpStatusException(response.getStatusCode(), response.getUri());
            }
        }

        request.setBody(terminalBody);
        request.setMethod(terminalMethod);
        return traversonClient.execute(request, returnType);
    }

}
