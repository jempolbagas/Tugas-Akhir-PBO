package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a financial transaction within the system.
 * This can be a stock purchase, sale, or a fund top-up.
 */
public class Transaksi {
    private String idTransaksi;
    private String jenis; // BUY atau SELL
    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private BigDecimal harga;
    private BigDecimal total;
    private java.time.LocalDateTime waktu;
    
    /**
     * Constructs a transaction for stock trading (Buy/Sell).
     * Automatically calculates the total value based on price and quantity.
     *
     * @param jenis      The type of transaction (e.g., "BUY", "SELL").
     * @param kodeSaham  The stock code involved.
     * @param namaSaham  The name of the stock.
     * @param jumlah     The quantity of shares.
     * @param harga      The price per share.
     */
    public Transaksi(String jenis, String kodeSaham, String namaSaham, 
                     int jumlah, BigDecimal harga) {
        this(jenis, kodeSaham, namaSaham, jumlah, harga, harga.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP));
    }

    /**
     * Constructs a transaction for non-stock operations like top-ups.
     * Sets default values for stock-related fields.
     *
     * @param jenis      The type of transaction (e.g., "TOPUP").
     * @param deskripsi  A description of the transaction (used as stock name placeholder).
     * @param total      The total amount involved.
     */
    public Transaksi(String jenis, String deskripsi, BigDecimal total) {
        this(jenis, "-", deskripsi, 1, total, total);
    }

    /**
     * Private constructor to initialize all fields.
     * Generates a unique transaction ID and timestamps the transaction.
     *
     * @param jenis      The type of transaction.
     * @param kodeSaham  The stock code.
     * @param namaSaham  The stock name or description.
     * @param jumlah     The quantity.
     * @param harga      The price per unit.
     * @param total      The total value.
     */
    private Transaksi(String jenis, String kodeSaham, String namaSaham, int jumlah, BigDecimal harga, BigDecimal total) {
        this.idTransaksi = "TRX" + System.currentTimeMillis();
        this.jenis = jenis;
        this.kodeSaham = kodeSaham;
        this.namaSaham = namaSaham;
        this.jumlah = jumlah;
        this.harga = harga;
        this.total = total;
        this.waktu = java.time.LocalDateTime.now();
    }
    
    /**
     * Gets the unique transaction ID.
     *
     * @return The transaction ID string.
     */
    public String getIdTransaksi() { return idTransaksi; }

    /**
     * Gets the type of transaction.
     *
     * @return The transaction type (e.g., "BUY", "SELL", "TOPUP").
     */
    public String getJenis() { return jenis; }

    /**
     * Gets the stock code involved in the transaction.
     *
     * @return The stock code.
     */
    public String getKodeSaham() { return kodeSaham; }

    /**
     * Gets the total monetary value of the transaction.
     *
     * @return The total value.
     */
    public BigDecimal getTotal() { return total; }

    /**
     * Gets the quantity of items (shares) involved.
     *
     * @return The quantity.
     */
    public int getJumlah() { return jumlah; }

    /**
     * Gets the price per unit (share) at the time of transaction.
     *
     * @return The price per unit.
     */
    public BigDecimal getHarga() { return harga; }

    /**
     * Gets the timestamp of the transaction.
     *
     * @return The LocalDateTime of the transaction.
     */
    public java.time.LocalDateTime getWaktu() { return waktu; }
    
    /**
     * Returns a formatted string representation of the transaction.
     * Includes ID, type, code, name, quantity, price, total, and timestamp.
     *
     * @return A string with transaction details formatted for display.
     */
    @Override
    public String toString() {
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%-15s %-6s %-8s %-25s %,6d @ Rp %,10.2f = Rp %,15.2f | %s",
            idTransaksi, jenis, kodeSaham, namaSaham, jumlah, harga, total, 
            waktu.format(formatter));
    }
}
