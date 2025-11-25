package StockTradingApp;

class SaldoTidakCukupException extends Exception {
    public SaldoTidakCukupException(String message) {
        super(message);
    }
}

class DatabaseLoadException extends Exception {
    public DatabaseLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}

class DatabaseSaveException extends Exception {
    public DatabaseSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

class AkunTidakDitemukanException extends Exception {
    public AkunTidakDitemukanException(String message) {
        super(message);
    }
}

class PasswordSalahException extends Exception {
    public PasswordSalahException(String message) {
        super(message);
    }
}

class SahamTidakDitemukanException extends Exception {
    public SahamTidakDitemukanException(String message) {
        super(message);
    }
}

class JumlahSahamTidakValidException extends Exception {
    public JumlahSahamTidakValidException(String message) {
        super(message);
    }
}