package main.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.exception.JumlahSahamTidakValidException;
import main.java.StockTradingApp.exception.SahamTidakDitemukanException;
import main.java.StockTradingApp.exception.SaldoTidakCukupException;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Portfolio;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.model.TradeResult;

import java.math.BigDecimal;

public class TradingService {
    private final MarketService marketService;
    private final AuthService auth;

    public TradingService(MarketService marketService, AuthService auth) {
        this.marketService = marketService;
        this.auth = auth;
    }

    /**
     * Attempts to buy the specified quantity of stock for the given account.
     *
     * @param akun The account performing the purchase.
     * @param ticker The stock ticker symbol.
     * @param quantity The number of shares to buy in sheets (lembar), not lots.
     * @return The result of the trade operation.
     */
    public TradeResult buyStock(Akun akun, String ticker, int quantity) {
        if (!marketService.isPasarBuka()) {
            return new TradeResult(false, "Pasar sedang tutup! Transaksi tidak dapat dilakukan.", akun);
        }

        try {
            Saham saham = marketService.getSaham(ticker);
            // Input quantity is in Sheets (Lembar). UI is responsible for Lot -> Sheet conversion.
            int jumlahLembar = quantity;

            if (jumlahLembar <= 0) {
                return new TradeResult(false, "Jumlah harus positif!", akun);
            }

            // Snapshot for rollback
            BigDecimal saldoSebelum = akun.getSaldo();
            Portfolio portfolioSebelum = akun.getPortfolio().get(saham.getKode());
            // Deep copy primitive/immutable values
            int jumlahPortfolioSebelum = portfolioSebelum != null ? portfolioSebelum.getJumlah() : 0;
            BigDecimal hargaBeliSebelum = portfolioSebelum != null ? portfolioSebelum.getHargaBeli() : BigDecimal.ZERO;
            BigDecimal totalModalSebelum = portfolioSebelum != null ? portfolioSebelum.getTotalModal() : BigDecimal.ZERO;
            int ukuranRiwayatSebelum = akun.getRiwayatTransaksi().size();

            // Execute on model
            akun.beliSaham(saham, jumlahLembar);

            // Persist
            try {
                auth.saveData();
                return new TradeResult(true, "Pembelian berhasil!", akun);
            } catch (DatabaseSaveException e) {
                // Rollback
                rollbackBeliSaham(akun, saham, saldoSebelum, jumlahPortfolioSebelum,
                                hargaBeliSebelum, totalModalSebelum, ukuranRiwayatSebelum);
                return new TradeResult(false, "Gagal menyimpan transaksi: " + e.getMessage(), akun);
            }

        } catch (SahamTidakDitemukanException | SaldoTidakCukupException e) {
            return new TradeResult(false, e.getMessage(), akun);
        } catch (Exception e) {
            return new TradeResult(false, "Terjadi kesalahan: " + e.getMessage(), akun);
        }
    }

    /**
     * Sells the specified quantity of shares (in sheets/lembar) for the given ticker from the user's account.
     *
     * @param akun    The account performing the sale.
     * @param ticker  The stock ticker symbol to sell.
     * @param quantity The number of shares to sell in sheets (lembar), not lots.
     * @return        The result of the trade.
     */
    public TradeResult sellStock(Akun akun, String ticker, int quantity) {
        if (!marketService.isPasarBuka()) {
            return new TradeResult(false, "Pasar sedang tutup! Transaksi tidak dapat dilakukan.", akun);
        }

        try {
            Saham saham = marketService.getSaham(ticker);
            // Assuming quantity is sheets

            if (quantity <= 0) {
                return new TradeResult(false, "Jumlah harus positif!", akun);
            }

            // Snapshot for rollback
            BigDecimal saldoSebelum = akun.getSaldo();
            Portfolio portfolioSebelum = akun.getPortfolio().get(saham.getKode());

            // If portfolio is null, we can't sell anyway, Akun.jualSaham will throw exception.
            // But we need to capture state if it exists.
            int jumlahPortfolioSebelum = 0;
            BigDecimal hargaBeliSebelum = BigDecimal.ZERO;
            BigDecimal totalModalSebelum = BigDecimal.ZERO;

            if (portfolioSebelum != null) {
                jumlahPortfolioSebelum = portfolioSebelum.getJumlah();
                hargaBeliSebelum = portfolioSebelum.getHargaBeli();
                totalModalSebelum = portfolioSebelum.getTotalModal();
            }

            int ukuranRiwayatSebelum = akun.getRiwayatTransaksi().size();

            // Execute on model
            akun.jualSaham(saham, quantity);

            // Persist
            try {
                auth.saveData();
                return new TradeResult(true, "Penjualan berhasil!", akun);
            } catch (DatabaseSaveException e) {
                // Rollback
                rollbackJualSaham(akun, saham, saldoSebelum, jumlahPortfolioSebelum,
                                hargaBeliSebelum, totalModalSebelum, ukuranRiwayatSebelum);
                return new TradeResult(false, "Gagal menyimpan transaksi: " + e.getMessage(), akun);
            }

        } catch (SahamTidakDitemukanException | JumlahSahamTidakValidException e) {
            return new TradeResult(false, e.getMessage(), akun);
        } catch (Exception e) {
            return new TradeResult(false, "Terjadi kesalahan: " + e.getMessage(), akun);
        }
    }

    private void rollbackBeliSaham(Akun akun, Saham saham, BigDecimal saldoSebelum,
                                          int jumlahPortfolioSebelum, BigDecimal hargaBeliSebelum,
                                          BigDecimal totalModalSebelum, int ukuranRiwayatSebelum) {
        // Restore saldo
        akun.setSaldo(saldoSebelum);

        // Restore portfolio
        if (jumlahPortfolioSebelum == 0) {
            // Portfolio didn't exist before, remove it
            akun.setPortfolioItem(saham.getKode(), null);
        } else {
            // Portfolio existed, restore to previous state
            Portfolio port = akun.getPortfolio().get(saham.getKode());
            if (port != null) {
                port.setJumlah(jumlahPortfolioSebelum);
                port.setHargaBeli(hargaBeliSebelum);
                port.setTotalModal(totalModalSebelum);
            }
        }

        // Remove the transaction that was added
        if (akun.getRiwayatTransaksi().size() > ukuranRiwayatSebelum) {
            akun.removeLastTransaction();
        }
    }

    private void rollbackJualSaham(Akun akun, Saham saham, BigDecimal saldoSebelum,
                                          int jumlahPortfolioSebelum, BigDecimal hargaBeliSebelum,
                                          BigDecimal totalModalSebelum, int ukuranRiwayatSebelum) {
        // Restore saldo
        akun.setSaldo(saldoSebelum);

        // Restore portfolio
        Portfolio port = akun.getPortfolio().get(saham.getKode());
        if (port == null) {
            // Portfolio was removed, recreate it
            akun.setPortfolioItem(saham.getKode(),
                new Portfolio(saham.getKode(), saham.getNamaSaham(),
                             jumlahPortfolioSebelum, hargaBeliSebelum));
            Portfolio restoredPort = akun.getPortfolio().get(saham.getKode());
            restoredPort.setTotalModal(totalModalSebelum);
        } else {
            // Portfolio still exists, restore to previous state
            port.setJumlah(jumlahPortfolioSebelum);
            port.setHargaBeli(hargaBeliSebelum);
            port.setTotalModal(totalModalSebelum);
        }

        // Remove the transaction that was added
        if (akun.getRiwayatTransaksi().size() > ukuranRiwayatSebelum) {
            akun.removeLastTransaction();
        }
    }
}
