package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Saham {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal MINIMUM_PRICE = new BigDecimal("50");

    private String kode;
    private String namaSaham;
    private String sektor;
    private BigDecimal hargaSekarang;
    private BigDecimal hargaBuka;
    private BigDecimal perubahan;
    private long volume;
    
    private List<BigDecimal> priceHistory;
    private List<String> timeHistory;

    public Saham(String kode, String namaSaham, String sektor, BigDecimal hargaAwal) {
        this.kode = kode;
        this.namaSaham = namaSaham;
        this.sektor = sektor;
        this.hargaSekarang = hargaAwal.setScale(2, RoundingMode.HALF_UP);
        this.hargaBuka = hargaAwal.setScale(2, RoundingMode.HALF_UP);
        this.perubahan = BigDecimal.ZERO;
        this.volume = 0;

        this.priceHistory = new ArrayList<>();
        this.timeHistory = new ArrayList<>();

        // Add initial state
        addToHistory(this.hargaSekarang);
    }
    
    // Getter methods
    public String getKode() { return kode; }
    public String getNamaSaham() { return namaSaham; }
    public String getSektor() { return sektor; }
    public BigDecimal getHargaSekarang() { return hargaSekarang; }
    public BigDecimal getHargaBuka() { return hargaBuka; }
    public BigDecimal getPerubahan() { return perubahan; }
    public long getVolume() { return volume; }
    public synchronized List<BigDecimal> getPriceHistory() { return new ArrayList<>(priceHistory); }
    public synchronized List<String> getTimeHistory() { return new ArrayList<>(timeHistory); }

    private synchronized void addToHistory(BigDecimal price) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(new Date());

        priceHistory.add(price);
        timeHistory.add(time);

        if (priceHistory.size() > 20) {
            priceHistory.remove(0);
            timeHistory.remove(0);
        }
    }
    
    // Update harga dengan random generator
    public synchronized void updateHarga(java.util.Random random) {
        // Perubahan harga antara -5% sampai +5%
        double persentasePerubahanDouble = (random.nextDouble() * 10) - 5; // -5 to +5
        BigDecimal persentasePerubahan = BigDecimal.valueOf(persentasePerubahanDouble).divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
        BigDecimal perubahanHarga = hargaSekarang.multiply(persentasePerubahan).setScale(2, RoundingMode.HALF_UP);
        
        hargaSekarang = hargaSekarang.add(perubahanHarga);
        if (hargaSekarang.compareTo(MINIMUM_PRICE) < 0) {
            hargaSekarang = MINIMUM_PRICE;
        }
        
        perubahan = hargaSekarang.subtract(hargaBuka)
                                 .divide(hargaBuka, 4, RoundingMode.HALF_UP)
                                 .multiply(ONE_HUNDRED)
                                 .setScale(2, RoundingMode.HALF_UP);
        volume += random.nextInt(1000000) + 100000; // Volume trading

        addToHistory(hargaSekarang);
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