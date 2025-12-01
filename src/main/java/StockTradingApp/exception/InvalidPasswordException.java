package main.java.StockTradingApp.exception;

/**
 * Exception thrown when a password does not meet the required criteria.
 */
public class InvalidPasswordException extends Exception {
    /**
     * Constructs a new InvalidPasswordException with the specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}
