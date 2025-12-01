package main.java.StockTradingApp.exception;

/**
 * Exception thrown when an account has insufficient funds to complete a transaction.
 */
public class SaldoTidakCukupException extends Exception {
    /**
     * Constructs a new SaldoTidakCukupException with the specified detail message.
     *
     * @param message The detail message.
     */
    public SaldoTidakCukupException(String message) {
        super(message);
    }
}
