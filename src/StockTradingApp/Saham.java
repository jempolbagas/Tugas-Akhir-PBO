package StockTradingApp;

class Saham {
    private String kode;
    private String namaSaham;
    private String sektor;
    private double hargaSekarang;
    private double hargaBuka;
    private double perubahan;
    private long volume;
    
    public Saham(String kode, String namaSaham, String sektor, double hargaAwal) {
        this.kode = kode;
        this.namaSaham = namaSaham;
        this.sektor = sektor;
        this.hargaSekarang = hargaAwal;
        this.hargaBuka = hargaAwal;
        this.perubahan = 0.0;
        this.volume = 0;
    }
    
    // Getter methods
    public String getKode() { return kode; }
    public String getNamaSaham() { return namaSaham; }
    public String getSektor() { return sektor; }
    public double getHargaSekarang() { return hargaSekarang; }
    public double getHargaBuka() { return hargaBuka; }
    public double getPerubahan() { return perubahan; }
    public long getVolume() { return volume; }
    
    // Update harga dengan random generator
    public void updateHarga(java.util.Random random) {
        // Perubahan harga antara -5% sampai +5%
        double persentasePerubahan = (random.nextDouble() * 10) - 5; // -5 to +5
        double perubahanHarga = hargaSekarang * (persentasePerubahan / 100);
        
        hargaSekarang += perubahanHarga;
        if (hargaSekarang < 50) hargaSekarang = 50; // Harga minimum
        
        perubahan = ((hargaSekarang - hargaBuka) / hargaBuka) * 100;
        volume += random.nextInt(1000000) + 100000; // Volume trading
    }
    
    public String getPerubahanFormatted() {
        return String.format("%s%.2f%%", perubahan >= 0 ? "+" : "", perubahan);
    }
    
    public String getStatusWarna() {
        if (perubahan > 0) return "🟢";
        else if (perubahan < 0) return "🔴";
        else return "⚪";
    }
    
    @Override
    public String toString() {
        return String.format("%-8s %-25s %-15s Rp %,12.2f %s %s", 
            kode, namaSaham, sektor, hargaSekarang, 
            getStatusWarna(), getPerubahanFormatted());
    }
}