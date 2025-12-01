package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Transaksi {
    private String idTransaksi;
    private String jenis; // BUY atau SELL
    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private BigDecimal harga;
    private BigDecimal total;
    private java.time.LocalDateTime waktu;
    
    public Transaksi(String jenis, String kodeSaham, String namaSaham, 
                     int jumlah, BigDecimal harga) {
        this(jenis, kodeSaham, namaSaham, jumlah, harga, harga.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP));
    }

    // Constructor for non-stock transactions like TOPUP
    public Transaksi(String jenis, String deskripsi, BigDecimal total) {
        this(jenis, "-", deskripsi, 1, total, total);
    }

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
    
    public String getIdTransaksi() { return idTransaksi; }
    public String getJenis() { return jenis; }
    public String getKodeSaham() { return kodeSaham; }
    public BigDecimal getTotal() { return total; }
    public int getJumlah() { return jumlah; }
    public BigDecimal getHarga() { return harga; }
    public java.time.LocalDateTime getWaktu() { return waktu; }
    
    @Override
    public String toString() {
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%-15s %-6s %-8s %-25s %,6d @ Rp %,10.2f = Rp %,15.2f | %s",
            idTransaksi, jenis, kodeSaham, namaSaham, jumlah, harga, total, 
            waktu.format(formatter));
    }
}
