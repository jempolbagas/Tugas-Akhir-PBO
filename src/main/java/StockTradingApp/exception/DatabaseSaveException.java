package main.java.StockTradingApp.exception;

/**
 * Exception thrown when there is an error saving data to the database/storage.
 */
public class DatabaseSaveException extends Exception {
    /**
     * Constructs a new DatabaseSaveException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause   The cause of the exception.
     */
    public DatabaseSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
