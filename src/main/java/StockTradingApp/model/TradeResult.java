package main.java.StockTradingApp.model;

public class TradeResult {
    private final boolean success;
    private final String message;
    private final Akun updatedAccount;

    public TradeResult(boolean success, String message, Akun updatedAccount) {
        this.success = success;
        this.message = message;
        this.updatedAccount = updatedAccount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Akun getUpdatedAccount() {
        return updatedAccount;
    }
}
