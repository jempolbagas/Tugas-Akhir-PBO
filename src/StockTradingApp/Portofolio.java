package StockTradingApp;

class Portfolio {
    private String kodeSaham;
    private String namaSaham;
    private int jumlah;
    private double hargaBeli;
    private double totalModal;
    
    public Portfolio(String kodeSaham, String namaSaham, int jumlah, double hargaBeli) {
        this.kodeSaham = kodeSaham;
        this.namaSaham = namaSaham;
        this.jumlah = jumlah;
        this.hargaBeli = hargaBeli;
        this.totalModal = jumlah * hargaBeli;
    }
    
    // Getter methods
    public String getKodeSaham() { return kodeSaham; }
    public String getNamaSaham() { return namaSaham; }
    public int getJumlah() { return jumlah; }
    public double getHargaBeli() { return hargaBeli; }
    public double getTotalModal() { return totalModal; }
    
    public void tambahJumlah(int tambahan, double hargaBaru) {
        double totalModalLama = totalModal;
        int jumlahBaru = jumlah + tambahan;
        double totalModalBaru = totalModalLama + (tambahan * hargaBaru);
        
        hargaBeli = totalModalBaru / jumlahBaru; // Average price
        jumlah = jumlahBaru;
        totalModal = totalModalBaru;
    }
    
    public void kurangiJumlah(int pengurangan) {
        jumlah -= pengurangan;
        totalModal = jumlah * hargaBeli;
    }
    
    public double hitungNilaiSekarang(double hargaSekarang) {
        return jumlah * hargaSekarang;
    }
    
    public double hitungKeuntungan(double hargaSekarang) {
        return hitungNilaiSekarang(hargaSekarang) - totalModal;
    }
    
    public double hitungPersentaseKeuntungan(double hargaSekarang) {
        return (hitungKeuntungan(hargaSekarang) / totalModal) * 100;
    }
    
    // Helper methods for rollback
    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }
    
    public void setHargaBeli(double hargaBeli) {
        this.hargaBeli = hargaBeli;
    }
    
    public void setTotalModal(double totalModal) {
        this.totalModal = totalModal;
    }
}