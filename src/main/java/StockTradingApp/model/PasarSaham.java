package main.java.StockTradingApp.model;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;

/**
 * Represents the stock market containing all available stocks.
 * Manages the list of stocks and simulates market activity.
 */
public class PasarSaham {
    private java.util.concurrent.ConcurrentHashMap<String, Saham> daftarSaham;
    private java.util.Random random;
    private boolean pasarBuka;
    
    /**
     * Constructs a new PasarSaham (Stock Market).
     * Initializes the market with a default set of stocks and opens the market.
     */
    public PasarSaham() {
        this.daftarSaham = new java.util.concurrent.ConcurrentHashMap<>();
        this.random = new java.util.Random();
        this.pasarBuka = true;
        initializeSaham();
    }
    
    /**
     * Initializes the default list of stocks in the market.
     * Adds various stocks across different sectors (Banking, Tech, Energy, Property).
     */
    private void initializeSaham() {
        // Saham Blue Chip
        tambahSaham(new Saham("BBCA", "Bank Central Asia", "Perbankan", new java.math.BigDecimal("8500")));
        tambahSaham(new Saham("BBRI", "Bank Rakyat Indonesia", "Perbankan", new java.math.BigDecimal("4500")));
        tambahSaham(new Saham("TLKM", "Telekomunikasi Indonesia", "Telekomunikasi", new java.math.BigDecimal("3200")));
        tambahSaham(new Saham("ASII", "Astra International", "Otomotif", new java.math.BigDecimal("5100")));
        tambahSaham(new Saham("UNVR", "Unilever Indonesia", "Konsumer", new java.math.BigDecimal("4200")));
        
        // Saham Tech
        tambahSaham(new Saham("GOTO", "GoTo Gojek Tokopedia", "Teknologi", new java.math.BigDecimal("1200")));
        tambahSaham(new Saham("BUKA", "Bukalapak", "E-Commerce", new java.math.BigDecimal("800")));
        
        // Saham Energi
        tambahSaham(new Saham("PGAS", "Perusahaan Gas Negara", "Energi", new java.math.BigDecimal("1500")));
        tambahSaham(new Saham("ADRO", "Adaro Energy", "Pertambangan", new java.math.BigDecimal("2800")));
        
        // Saham Property
        tambahSaham(new Saham("BSDE", "Bumi Serpong Damai", "Property", new java.math.BigDecimal("1100")));
    }
    
    /**
     * Adds a stock to the market.
     *
     * @param saham The stock (Saham) to add.
     */
    private void tambahSaham(Saham saham) {
        daftarSaham.put(saham.getKode(), saham);
    }
    
    /**
     * Updates the prices of all stocks in the market.
     * This simulates market fluctuations.
     */
    public void updateHargaSemua() {
        for (Saham saham : daftarSaham.values()) {
            saham.updateHarga(random);
        }
    }
    
    /**
     * Retrieves a stock by its code.
     *
     * @param kode The stock code (e.g., "BBCA").
     * @return The Saham object corresponding to the code.
     * @throws SahamTidakDitemukanException if the stock code is not found.
     */
    public Saham getSaham(String kode) throws SahamTidakDitemukanException {
        if (!daftarSaham.containsKey(kode.toUpperCase())) {
            throw new SahamTidakDitemukanException("Kode saham tidak ditemukan!");
        }
        return daftarSaham.get(kode.toUpperCase());
    }
    
    /**
     * Retrieves all available stocks in the market.
     *
     * @return An ArrayList containing all Saham objects.
     */
    public java.util.ArrayList<Saham> getAllSaham() {
        return new java.util.ArrayList<>(daftarSaham.values());
    }
    
    /**
     * Checks if the market is currently open for trading.
     *
     * @return true if the market is open, false otherwise.
     */
    public boolean isPasarBuka() { return pasarBuka; }

    /**
     * Opens the market for trading.
     */
    public void bukaPasar() { pasarBuka = true; }

    /**
     * Closes the market, preventing further trading.
     */
    public void tutupPasar() { pasarBuka = false; }
}
