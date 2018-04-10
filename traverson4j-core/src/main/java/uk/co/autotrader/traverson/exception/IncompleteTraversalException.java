package uk.co.autotrader.traverson.exception;

public abstract class IncompleteTraversalException extends RuntimeException {
    IncompleteTraversalException(String message) {
        super(message);
    }
}
