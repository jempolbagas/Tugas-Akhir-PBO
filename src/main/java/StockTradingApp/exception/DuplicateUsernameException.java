package main.java.StockTradingApp.exception;

/**
 * Exception thrown when attempting to register with a username that already exists.
 */
public class DuplicateUsernameException extends Exception {
    /**
     * Constructs a new DuplicateUsernameException with the specified detail message.
     *
     * @param message The detail message.
     */
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
