package StockTradingApp;

class SaldoTidakCukupException extends Exception {
    public SaldoTidakCukupException(String message) {
        super(message);
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