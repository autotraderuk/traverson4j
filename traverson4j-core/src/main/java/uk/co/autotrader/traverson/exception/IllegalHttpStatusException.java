package uk.co.autotrader.traverson.exception;

import java.net.URI;

/**
 * <p>Thrown when a http response part way traversing has status code other than the 2xx family</p>
 */
public class IllegalHttpStatusException extends IncompleteTraversalException {
    private final int statusCode;
    private final URI uri;

    /**
     * Constructs a IllegalHttpStatusException
     * @param statusCode the failing http status code
     * @param uri the uri that caused the failure
     */
    public IllegalHttpStatusException(int statusCode, URI uri) {
        super(String.format("Received status code %d from url %s", statusCode, uri));
        this.statusCode = statusCode;
        this.uri = uri;
    }

    /**
     * @return the returned status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the url that failed http GET
     */
    public URI getUri() {
        return uri;
    }
}
