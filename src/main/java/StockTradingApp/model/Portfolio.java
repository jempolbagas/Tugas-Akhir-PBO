package main.java.StockTradingApp.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Portfolio {
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private BigDecimal hargaBeli;
    private BigDecimal totalModal;

    public Portfolio(String kodeSaham, String namaSaham, int jumlah, BigDecimal hargaBeli) {
        this.kodeSaham = kodeSaham;
        this.namaSaham = namaSaham;
        this.jumlah = jumlah;
        this.hargaBeli = hargaBeli.setScale(2, RoundingMode.HALF_UP);
        this.totalModal = this.hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    // Getter methods
    public String getKodeSaham() { return kodeSaham; }
    public String getNamaSaham() { return namaSaham; }
    public int getJumlah() { return jumlah; }
    public BigDecimal getHargaBeli() { return hargaBeli; }
    public BigDecimal getTotalModal() { return totalModal; }

    public void tambahJumlah(int tambahan, BigDecimal hargaBaru) {
        BigDecimal totalModalLama = totalModal;
        int jumlahBaru = jumlah + tambahan;
        BigDecimal tambahanModal = hargaBaru.multiply(BigDecimal.valueOf(tambahan));
        BigDecimal totalModalBaru = totalModalLama.add(tambahanModal);

        hargaBeli = totalModalBaru.divide(BigDecimal.valueOf(jumlahBaru), 2, RoundingMode.HALF_UP);
        jumlah = jumlahBaru;
        totalModal = totalModalBaru.setScale(2, RoundingMode.HALF_UP);
    }

    public void kurangiJumlah(int pengurangan) {
        jumlah -= pengurangan;
        totalModal = hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    public void tambahJumlahTanpaAvg(int tambahan) {
        jumlah += tambahan;
        totalModal = hargaBeli.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal hitungNilaiSekarang(BigDecimal hargaSekarang) {
        return hargaSekarang.multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal hitungKeuntungan(BigDecimal hargaSekarang) {
        return hitungNilaiSekarang(hargaSekarang).subtract(totalModal);
    }

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
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public void setTotalModal(BigDecimal totalModal) {
        this.totalModal = totalModal;
    }
}
