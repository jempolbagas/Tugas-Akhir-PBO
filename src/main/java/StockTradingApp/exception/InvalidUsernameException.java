package main.java.StockTradingApp.exception;

/**
 * Exception thrown when a username is invalid (e.g. empty or contains illegal characters).
 */
public class InvalidUsernameException extends Exception {
    /**
     * Constructs a new InvalidUsernameException with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidUsernameException(String message) {
        super(message);
    }
}
