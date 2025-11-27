package main.java.StockTradingApp.model;

import main.java.StockTradingApp.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Akun {
    private String username;
    private String password;
    private String namaLengkap;
    private String email;
    private BigDecimal saldo;
    private java.util.HashMap<String, Portfolio> portfolio;
    private java.util.ArrayList<Transaksi> riwayatTransaksi;
    private java.time.LocalDateTime tanggalBuat;

    public Akun(String username, String password, String namaLengkap, String email, BigDecimal saldoAwal) {
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.saldo = saldoAwal.setScale(2, RoundingMode.HALF_UP);
        this.portfolio = new java.util.HashMap<>();
        this.riwayatTransaksi = new java.util.ArrayList<>();
        this.tanggalBuat = java.time.LocalDateTime.now();
    }

    // Getter methods
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getEmail() { return email; }
    public BigDecimal getSaldo() { return saldo; }
    public java.util.HashMap<String, Portfolio> getPortfolio() { return portfolio; }
    public java.util.ArrayList<Transaksi> getRiwayatTransaksi() { return riwayatTransaksi; }
    public java.time.LocalDateTime getTanggalBuat() { return tanggalBuat; }

    public void tambahSaldo(BigDecimal jumlah) {
        if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif!");
        }
        this.saldo = this.saldo.add(jumlah);
        riwayatTransaksi.add(new Transaksi("TOPUP", "Setor Dana", jumlah));
    }

    public void beliSaham(Saham saham, int jumlah) throws SaldoTidakCukupException {
        BigDecimal totalHarga = saham.getHargaSekarang().multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);

        if (saldo.compareTo(totalHarga) < 0) {
            throw new SaldoTidakCukupException("Saldo tidak cukup! Dibutuhkan: Rp " +
                String.format("%,.2f", totalHarga));
        }

        saldo = saldo.subtract(totalHarga);

        // Update portfolio
        if (portfolio.containsKey(saham.getKode())) {
            portfolio.get(saham.getKode()).tambahJumlah(jumlah, saham.getHargaSekarang());
        } else {
            portfolio.put(saham.getKode(),
                new Portfolio(saham.getKode(), saham.getNamaSaham(), jumlah, saham.getHargaSekarang()));
        }

        // Catat transaksi
        riwayatTransaksi.add(new Transaksi("BUY", saham.getKode(),
            saham.getNamaSaham(), jumlah, saham.getHargaSekarang()));
    }

    public void jualSaham(Saham saham, int jumlah) throws JumlahSahamTidakValidException {
        if (!portfolio.containsKey(saham.getKode())) {
            throw new JumlahSahamTidakValidException("Anda tidak memiliki saham " + saham.getKode());
        }

        Portfolio port = portfolio.get(saham.getKode());
        if (port.getJumlah() < jumlah) {
            throw new JumlahSahamTidakValidException("Jumlah saham tidak cukup! Anda hanya punya: " +
                port.getJumlah() + " lembar");
        }

        BigDecimal totalHarga = saham.getHargaSekarang().multiply(BigDecimal.valueOf(jumlah)).setScale(2, RoundingMode.HALF_UP);
        saldo = saldo.add(totalHarga);

        port.kurangiJumlah(jumlah);
        if (port.getJumlah() == 0) {
            portfolio.remove(saham.getKode());
        }

        // Catat transaksi
        riwayatTransaksi.add(new Transaksi("SELL", saham.getKode(),
            saham.getNamaSaham(), jumlah, saham.getHargaSekarang()));
    }

    // Helper methods for rollback
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void removeLastTransaction() {
        if (!riwayatTransaksi.isEmpty()) {
            riwayatTransaksi.remove(riwayatTransaksi.size() - 1);
        }
    }

    public void setPortfolioItem(String kode, Portfolio portfolio) {
        if (portfolio == null) {
            this.portfolio.remove(kode);
        } else {
            this.portfolio.put(kode, portfolio);
        }
    }
}