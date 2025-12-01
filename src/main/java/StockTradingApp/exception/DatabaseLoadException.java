package main.java.StockTradingApp.exception;

/**
 * Exception thrown when there is an error loading data from the database/storage.
 */
public class DatabaseLoadException extends Exception {
    /**
     * Constructs a new DatabaseLoadException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public DatabaseLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
