package main.java.StockTradingApp.exception;

public class DatabaseLoadException extends Exception {
    public DatabaseLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
