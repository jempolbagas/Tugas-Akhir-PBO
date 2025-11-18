package StockTradingApp;

class Transaksi implements java.io.Serializable {
    private String idTransaksi;
    private String jenis; // BUY atau SELL
    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private double harga;
    private double total;
    private java.time.LocalDateTime waktu;
    
    public Transaksi(String jenis, String kodeSaham, String namaSaham, 
                     int jumlah, double harga) {
        this.idTransaksi = "TRX" + System.currentTimeMillis();
        this.jenis = jenis;
        this.kodeSaham = kodeSaham;
        this.namaSaham = namaSaham;
        this.jumlah = jumlah;
        this.harga = harga;
        this.total = jumlah * harga;
        this.waktu = java.time.LocalDateTime.now();
    }
    
    public String getJenis() { return jenis; }
    public String getKodeSaham() { return kodeSaham; }
    public double getTotal() { return total; }
    
    @Override
    public String toString() {
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("%-15s %-6s %-8s %-25s %,6d @ Rp %,10.2f = Rp %,15.2f | %s",
            idTransaksi, jenis, kodeSaham, namaSaham, jumlah, harga, total, 
            waktu.format(formatter));
    }
}
