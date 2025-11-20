package StockTradingApp;

class Akun {
    private String username;
    private String password;
    private String namaLengkap;
    private String email;
    private double saldo;
    private java.util.HashMap<String, Portfolio> portfolio;
    private java.util.ArrayList<Transaksi> riwayatTransaksi;
    private java.time.LocalDateTime tanggalBuat;
    
    public Akun(String username, String password, String namaLengkap, String email, double saldoAwal) {
        this.username = username;
        this.password = password;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.saldo = saldoAwal;
        this.portfolio = new java.util.HashMap<>();
        this.riwayatTransaksi = new java.util.ArrayList<>();
        this.tanggalBuat = java.time.LocalDateTime.now();
    }
    
    // Getter methods
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getEmail() { return email; }
    public double getSaldo() { return saldo; }
    public java.util.HashMap<String, Portfolio> getPortfolio() { return portfolio; }
    public java.util.ArrayList<Transaksi> getRiwayatTransaksi() { return riwayatTransaksi; }
    public java.time.LocalDateTime getTanggalBuat() { return tanggalBuat; }
    
    public void tambahSaldo(double jumlah) {
        if (jumlah <= 0) throw new IllegalArgumentException("Jumlah harus positif!");
        this.saldo += jumlah;
        riwayatTransaksi.add(new Transaksi("TOPUP", "-", "-", 0, jumlah));
    }
    
    public void beliSaham(Saham saham, int jumlah) throws SaldoTidakCukupException {
        double totalHarga = saham.getHargaSekarang() * jumlah;
        
        if (saldo < totalHarga) {
            throw new SaldoTidakCukupException("Saldo tidak cukup! Dibutuhkan: Rp " + 
                String.format("%,.2f", totalHarga));
        }
        
        saldo -= totalHarga;
        
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
        
        double totalHarga = saham.getHargaSekarang() * jumlah;
        saldo += totalHarga;
        
        port.kurangiJumlah(jumlah);
        if (port.getJumlah() == 0) {
            portfolio.remove(saham.getKode());
        }
        
        // Catat transaksi
        riwayatTransaksi.add(new Transaksi("SELL", saham.getKode(), 
            saham.getNamaSaham(), jumlah, saham.getHargaSekarang()));
    }

    public void rollbackLastTransaction() {
        if (riwayatTransaksi.isEmpty()) {
            return;
        }

        Transaksi lastTx = riwayatTransaksi.remove(riwayatTransaksi.size() - 1);

        if ("BUY".equals(lastTx.getJenis())) {
            // Rollback a buy transaction
            saldo += lastTx.getTotal();
            Portfolio port = portfolio.get(lastTx.getKodeSaham());
            if (port != null) {
                port.kurangiJumlah(lastTx.getJumlah());
                if (port.getJumlah() == 0) {
                    portfolio.remove(lastTx.getKodeSaham());
                }
            }
        } else if ("SELL".equals(lastTx.getJenis())) {
            // Rollback a sell transaction
            saldo -= lastTx.getTotal();
            Portfolio port = portfolio.get(lastTx.getKodeSaham());
            if (port != null) {
                port.tambahJumlahTanpaAvg(lastTx.getJumlah());
            } else {
                // This case is unlikely if the logic is correct, but we handle it
                // We don't have the original buy price here, so we can't perfectly restore it.
                // This is a limitation of the current design.
            }
        } else if ("TOPUP".equals(lastTx.getJenis())) {
            // Rollback a top-up
            saldo -= lastTx.getTotal();
        }
    }
}