package main.java.StockTradingApp.model;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;

public class PasarSaham {
    private java.util.concurrent.ConcurrentHashMap<String, Saham> daftarSaham;
    private java.util.Random random;
    private boolean pasarBuka;
    
    public PasarSaham() {
        this.daftarSaham = new java.util.concurrent.ConcurrentHashMap<>();
        this.random = new java.util.Random();
        this.pasarBuka = true;
        initializeSaham();
    }
    
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
    
    private void tambahSaham(Saham saham) {
        daftarSaham.put(saham.getKode(), saham);
    }
    
    public void updateHargaSemua() {
        for (Saham saham : daftarSaham.values()) {
            saham.updateHarga(random);
        }
    }
    
    public Saham getSaham(String kode) throws SahamTidakDitemukanException {
        if (!daftarSaham.containsKey(kode.toUpperCase())) {
            throw new SahamTidakDitemukanException("Kode saham tidak ditemukan!");
        }
        return daftarSaham.get(kode.toUpperCase());
    }
    
    public java.util.ArrayList<Saham> getAllSaham() {
        return new java.util.ArrayList<>(daftarSaham.values());
    }
    
    public boolean isPasarBuka() { return pasarBuka; }
    public void bukaPasar() { pasarBuka = true; }
    public void tutupPasar() { pasarBuka = false; }
}