package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a stock holding in a user's portfolio.
 * Tracks the stock details, quantity owned, and acquisition costs.
 */
public class Portfolio {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private BigDecimal hargaBeli;
    private BigDecimal totalModal;

    /**
     * Constructs a new Portfolio item.
     *
     * @param kodeSaham  The stock code.
     * @param namaSaham  The name of the stock.
     * @param jumlah     The number of shares.
     * @param hargaBeli  The average purchase price per share.
     */
    public Portfolio(String kodeSaham, String namaSaham, int jumlah, BigDecimal hargaBeli) {
        this.kodeSaham = kodeSaham;
        this.namaSaham = namaSaham;
        this.jumlah = jumlah;
        this.hargaBeli = hargaBeli.setScale(2, RoundingMode.HALF_UP);
        this.totalModal = this.hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Gets the stock code.
     *
     * @return The stock code string.
     */
    public String getKodeSaham() { return kodeSaham; }

    /**
     * Gets the name of the stock.
     *
     * @return The stock name string.
     */
    public String getNamaSaham() { return namaSaham; }

    /**
     * Gets the number of shares held.
     *
     * @return The quantity of shares.
     */
    public int getJumlah() { return jumlah; }

    /**
     * Gets the average purchase price per share.
     *
     * @return The average purchase price.
     */
    public BigDecimal getHargaBeli() { return hargaBeli; }

    /**
     * Gets the total capital invested in this stock.
     *
     * @return The total investment value.
     */
    public BigDecimal getTotalModal() { return totalModal; }

    /**
     * Increases the number of shares held and updates the average purchase price.
     * This uses the weighted average method.
     *
     * @param tambahan   The number of additional shares bought.
     * @param hargaBaru  The price per share for the new purchase.
     */
    public void tambahJumlah(int tambahan, BigDecimal hargaBaru) {
        BigDecimal totalModalLama = totalModal;
        int jumlahBaru = jumlah + tambahan;
        BigDecimal tambahanModal = hargaBaru.multiply(BigDecimal.valueOf(tambahan));
        BigDecimal totalModalBaru = totalModalLama.add(tambahanModal);

        hargaBeli = totalModalBaru.divide(BigDecimal.valueOf(jumlahBaru), 2, RoundingMode.HALF_UP);
        jumlah = jumlahBaru;
        totalModal = totalModalBaru.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Decreases the number of shares held.
     * Updates the total capital invested based on the current average price.
     *
     * @param pengurangan The number of shares to sell.
     */
    public void kurangiJumlah(int pengurangan) {
        jumlah -= pengurangan;
        totalModal = hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Increases the number of shares without updating the average price.
     * This assumes the additional shares have the same cost basis or is used for simple adjustments.
     *
     * @param tambahan The number of additional shares.
     */
    public void tambahJumlahTanpaAvg(int tambahan) {
        jumlah += tambahan;
        totalModal = hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the current market value of this holding.
     *
     * @param hargaSekarang The current price per share.
     * @return The total current value (price * quantity).
     */
    public BigDecimal hitungNilaiSekarang(BigDecimal hargaSekarang) {
        return hargaSekarang.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the profit or loss based on the current price.
     *
     * @param hargaSekarang The current price per share.
     * @return The monetary profit (positive) or loss (negative).
     */
    public BigDecimal hitungKeuntungan(BigDecimal hargaSekarang) {
        return hitungNilaiSekarang(hargaSekarang).subtract(totalModal);
    }

    /**
     * Calculates the percentage profit or loss.
     *
     * @param hargaSekarang The current price per share.
     * @return The percentage profit (positive) or loss (negative).
     */
    public BigDecimal hitungPersentaseKeuntungan(BigDecimal hargaSekarang) {
        if (totalModal.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalStateException("Total modal is zero. This indicates an invalid portfolio state.");
        }
        return hitungKeuntungan(hargaSekarang)
                .divide(totalModal, 4, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Helper methods for rollback

    /**
     * Sets the quantity of shares directly.
     * Typically used for rollback operations.
     *
     * @param jumlah The new quantity of shares.
     */
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    /**
     * Sets the average purchase price directly.
     * Typically used for rollback operations.
     *
     * @param hargaBeli The new average purchase price.
     */
    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    /**
     * Sets the total capital invested directly.
     * Typically used for rollback operations.
     *
     * @param totalModal The new total capital.
     */
    public void setTotalModal(BigDecimal totalModal) {
        this.totalModal = totalModal;
    }
}
