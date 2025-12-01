package main.java.StockTradingApp.model;

/**
 * Represents the result of a trade operation (buy or sell).
 * Encapsulates the success status, a message, and the updated account state.
 */
public class TradeResult {
    private final boolean success;
    private final String message;
    private final Akun updatedAccount;

    /**
     * Constructs a new TradeResult.
     *
     * @param success        Indicates if the trade was successful.
     * @param message        A message describing the result or error.
     * @param updatedAccount The account object reflecting the state after the trade.
     */
    public TradeResult(boolean success, String message, Akun updatedAccount) {
        this.success = success;
        this.message = message;
        this.updatedAccount = updatedAccount;
    }

    /**
     * Checks if the trade was successful.
     *
     * @return true if successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the result message.
     *
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the updated account object.
     *
     * @return The Akun object after the trade.
     */
    public Akun getUpdatedAccount() {
        return updatedAccount;
    }
}
