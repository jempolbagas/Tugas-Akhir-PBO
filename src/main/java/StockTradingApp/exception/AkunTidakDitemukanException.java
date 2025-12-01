package main.java.StockTradingApp.exception;

/**
 * Exception thrown when an account cannot be found in the system.
 */
public class AkunTidakDitemukanException extends Exception {
    /**
     * Constructs a new AkunTidakDitemukanException with the specified detail message.
     *
     * @param message The detail message.
     */
    public AkunTidakDitemukanException(String message) {
        super(message);
    }
}
