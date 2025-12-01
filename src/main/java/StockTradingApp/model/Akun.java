package main.java.StockTradingApp.model;

import main.java.StockTradingApp.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents a user account in the stock trading application.
 * This class manages user details, account balance, stock portfolio, and transaction history.
 */
public class Akun {
    private String username;
    private String password;
    private String namaLengkap;
    private String email;
    private BigDecimal saldo;
    private java.util.HashMap<String, Portfolio> portfolio;
    private java.util.ArrayList<Transaksi> riwayatTransaksi;
    private java.time.LocalDateTime tanggalBuat;

    /**
     * Constructs a new Akun (Account) with the specified details.
     *
     * @param username     The unique username for the account.
     * @param password     The password for the account authentication.
     * @param namaLengkap  The full name of the account holder.
     * @param email        The email address of the account holder.
     * @param saldoAwal    The initial monetary balance for the account.
     */
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

    /**
     * Gets the username of the account.
     *
     * @return The username string.
     */
    public String getUsername() { return username; }

    /**
     * Gets the password of the account.
     *
     * @return The password string.
     */
    public String getPassword() { return password; }

    /**
     * Gets the full name of the account holder.
     *
     * @return The full name string.
     */
    public String getNamaLengkap() { return namaLengkap; }

    /**
     * Gets the email address of the account holder.
     *
     * @return The email string.
     */
    public String getEmail() { return email; }

    /**
     * Gets the current balance of the account.
     *
     * @return The current balance as a BigDecimal.
     */
    public BigDecimal getSaldo() { return saldo; }

    /**
     * Gets the user's stock portfolio.
     *
     * @return A HashMap where keys are stock codes and values are Portfolio objects.
     */
    public java.util.HashMap<String, Portfolio> getPortfolio() { return portfolio; }

    /**
     * Gets the transaction history of the account.
     *
     * @return An ArrayList of Transaksi objects representing past transactions.
     */
    public java.util.ArrayList<Transaksi> getRiwayatTransaksi() { return riwayatTransaksi; }

    /**
     * Gets the timestamp when the account was created.
     *
     * @return The LocalDateTime of account creation.
     */
    public java.time.LocalDateTime getTanggalBuat() { return tanggalBuat; }

    /**
     * Adds funds to the account balance.
     *
     * @param jumlah The amount of money to add. Must be positive.
     * @throws IllegalArgumentException if the amount is zero or negative.
     */
    public void tambahSaldo(BigDecimal jumlah) {
        if (jumlah.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Jumlah harus positif!");
        }
        this.saldo = this.saldo.add(jumlah);
        riwayatTransaksi.add(new Transaksi("TOPUP", "Setor Dana", jumlah));
    }

    /**
     * Buys a specified amount of stocks.
     * Deducts the cost from the balance and updates the portfolio.
     *
     * @param saham  The stock (Saham) to purchase.
     * @param jumlah The number of shares to buy.
     * @throws SaldoTidakCukupException if the account balance is insufficient.
     */
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

    /**
     * Sells a specified amount of stocks.
     * Adds the proceeds to the balance and updates the portfolio.
     *
     * @param saham  The stock (Saham) to sell.
     * @param jumlah The number of shares to sell.
     * @throws JumlahSahamTidakValidException if the user does not own the stock or has insufficient shares.
     */
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

    /**
     * Sets the account balance directly.
     * Typically used for rollback operations in case of transaction failure.
     *
     * @param saldo The new balance to set.
     */
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    /**
     * Removes the last transaction from the history.
     * Typically used for rollback operations.
     */
    public void removeLastTransaction() {
        if (!riwayatTransaksi.isEmpty()) {
            riwayatTransaksi.remove(riwayatTransaksi.size() - 1);
        }
    }

    /**
     * Sets or updates a portfolio item directly.
     * If the portfolio object is null, the item is removed.
     * Typically used for rollback operations.
     *
     * @param kode      The stock code.
     * @param portfolio The Portfolio object to set, or null to remove.
     */
    public void setPortfolioItem(String kode, Portfolio portfolio) {
        if (portfolio == null) {
            this.portfolio.remove(kode);
        } else {
            this.portfolio.put(kode, portfolio);
        }
    }
}
