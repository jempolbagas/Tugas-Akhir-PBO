package StockTradingApp;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Saham {
    private String kode;
    private String namaSaham;
    private String sektor;
    private BigDecimal hargaSekarang;
    private BigDecimal hargaBuka;
    private BigDecimal perubahan;
    private long volume;
    
    public Saham(String kode, String namaSaham, String sektor, double hargaAwal) {
        this.kode = kode;
        this.namaSaham = namaSaham;
        this.sektor = sektor;
        this.hargaSekarang = BigDecimal.valueOf(hargaAwal).setScale(2, RoundingMode.HALF_UP);
        this.hargaBuka = BigDecimal.valueOf(hargaAwal).setScale(2, RoundingMode.HALF_UP);
        this.perubahan = BigDecimal.ZERO;
        this.volume = 0;
    }
    
    // Getter methods
    public String getKode() { return kode; }
    public String getNamaSaham() { return namaSaham; }
    public String getSektor() { return sektor; }
    public BigDecimal getHargaSekarang() { return hargaSekarang; }
    public BigDecimal getHargaBuka() { return hargaBuka; }
    public BigDecimal getPerubahan() { return perubahan; }
    public long getVolume() { return volume; }
    
    // Update harga dengan random generator
    public void updateHarga(java.util.Random random) {
        // Perubahan harga antara -5% sampai +5%
        double persentasePerubahanDouble = (random.nextDouble() * 10) - 5; // -5 to +5
        BigDecimal persentasePerubahan = BigDecimal.valueOf(persentasePerubahanDouble).divide(new BigDecimal("100"));
        BigDecimal perubahanHarga = hargaSekarang.multiply(persentasePerubahan).setScale(2, RoundingMode.HALF_UP);
        
        hargaSekarang = hargaSekarang.add(perubahanHarga);
        if (hargaSekarang.compareTo(new BigDecimal("50")) < 0) {
            hargaSekarang = new BigDecimal("50").setScale(2, RoundingMode.HALF_UP);
        }
        
        perubahan = hargaSekarang.subtract(hargaBuka)
                                 .divide(hargaBuka, 4, RoundingMode.HALF_UP)
                                 .multiply(new BigDecimal("100"))
                                 .setScale(2, RoundingMode.HALF_UP);
        volume += random.nextInt(1000000) + 100000; // Volume trading
    }
    
    public String getPerubahanFormatted() {
        return String.format("%s%.2f%%", perubahan.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", perubahan);
    }
    
    public String getStatusWarna() {
        if (perubahan.compareTo(BigDecimal.ZERO) > 0) return "🟢";
        else if (perubahan.compareTo(BigDecimal.ZERO) < 0) return "🔴";
        else return "⚪";
    }
    
    @Override
    public String toString() {
        return String.format("%-8s %-25s %-15s Rp %,12.2f %s %s", 
            kode, namaSaham, sektor, hargaSekarang, 
            getStatusWarna(), getPerubahanFormatted());
    }
}