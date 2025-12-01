package main.java.StockTradingApp.exception;

/**
 * Exception thrown when the provided password for authentication is incorrect.
 */
public class PasswordSalahException extends Exception {
    /**
     * Constructs a new PasswordSalahException with the specified detail message.
     *
     * @param message The detail message.
     */
    public PasswordSalahException(String message) {
        super(message);
    }
}
