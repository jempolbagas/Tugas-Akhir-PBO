package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a stock (share) in the market.
 * Contains information about price, sector, and price history.
 */
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

    /**
     * Constructs a new Saham object.
     *
     * @param kode       The stock code (ticker symbol).
     * @param namaSaham  The name of the company.
     * @param sektor     The business sector.
     * @param hargaAwal  The initial price.
     */
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
    
    /**
     * Gets the stock code (ticker).
     *
     * @return The stock code.
     */
    public String getKode() { return kode; }

    /**
     * Gets the full name of the stock.
     *
     * @return The stock name.
     */
    public String getNamaSaham() { return namaSaham; }

    /**
     * Gets the sector the stock belongs to.
     *
     * @return The sector name.
     */
    public String getSektor() { return sektor; }

    /**
     * Gets the current market price of the stock.
     *
     * @return The current price.
     */
    public BigDecimal getHargaSekarang() { return hargaSekarang; }

    /**
     * Gets the price of the stock when the market opened.
     *
     * @return The opening price.
     */
    public BigDecimal getHargaBuka() { return hargaBuka; }

    /**
     * Gets the percentage change in price since opening.
     *
     * @return The percentage change.
     */
    public BigDecimal getPerubahan() { return perubahan; }

    /**
     * Gets the trading volume.
     *
     * @return The total volume traded.
     */
    public long getVolume() { return volume; }

    /**
     * Gets the history of prices for this stock.
     *
     * @return A list of historical prices.
     */
    public synchronized List<BigDecimal> getPriceHistory() { return new ArrayList<>(priceHistory); }

    /**
     * Gets the timestamps corresponding to the price history.
     *
     * @return A list of timestamp strings.
     */
    public synchronized List<String> getTimeHistory() { return new ArrayList<>(timeHistory); }

    /**
     * Adds a price point to the history.
     * Maintains a fixed size history window.
     *
     * @param price The price to add.
     */
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
    
    /**
     * Updates the stock price using a random fluctuation simulation.
     * Also updates the price history and volume.
     *
     * @param random The Random instance to use for fluctuation calculation.
     */
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
    
    /**
     * Formats the percentage change in price.
     *
     * @return A string representing the percentage change (e.g., "+1.50%").
     */
    public String getPerubahanFormatted() {
        return String.format("%s%.2f%%", perubahan.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", perubahan);
    }
    
    /**
     * Gets a status icon representing the price trend.
     *
     * @return "🟢" for up, "🔴" for down, "⚪" for unchanged.
     */
    public String getStatusWarna() {
        if (perubahan.compareTo(BigDecimal.ZERO) > 0) return "🟢";
        else if (perubahan.compareTo(BigDecimal.ZERO) < 0) return "🔴";
        else return "⚪";
    }
    
    /**
     * Returns a string representation of the stock.
     *
     * @return A formatted string with code, name, sector, price, and status.
     */
    @Override
    public String toString() {
        return String.format("%-8s %-25s %-15s Rp %,12.2f %s %s", 
            kode, namaSaham, sektor, hargaSekarang, 
            getStatusWarna(), getPerubahanFormatted());
    }
}
