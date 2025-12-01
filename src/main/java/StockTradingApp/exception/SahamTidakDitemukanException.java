package main.java.StockTradingApp.exception;

/**
 * Exception thrown when a requested stock cannot be found in the market.
 */
public class SahamTidakDitemukanException extends Exception {
    /**
     * Constructs a new SahamTidakDitemukanException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SahamTidakDitemukanException(String message) {
        super(message);
    }
}
