
package uk.co.autotrader.traverson.exception;
/**
 * <p>Thrown when an issue has occurred performing a http call</p>
 * <p>You may consider retrying this request</p>
 */
public class HttpException extends RuntimeException {
    /**
     * Constructs a HttpException with just the message
     * @param message detailed message of the error
     */
    public HttpException(String message) {
        super(message);
    }

    /**
     * Overloaded Construtor - Constructs a HttpException with the message and cause
     * @param message detailed message of the error
     * @param cause the original exception for this HttpException
     */
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }
}
