package main.java.StockTradingApp.exception;

/**
 * Exception thrown when the quantity of stocks for a transaction is invalid.
 * For example, selling more shares than owned.
 */
public class JumlahSahamTidakValidException extends Exception {
    /**
     * Constructs a new JumlahSahamTidakValidException with the specified detail message.
     *
     * @param message The detail message.
     */
    public JumlahSahamTidakValidException(String message) {
        super(message);
    }
}
