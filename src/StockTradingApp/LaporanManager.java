package StockTradingApp;

class LaporanManager {
    public static void exportLaporan(Akun akun, PasarSaham pasar) {
        String filename = "Laporan_Trading_" + akun.getUsername() + ".txt";
        
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            writer.println("═".repeat(100));
            writer.println(UIHelper.centerText("LAPORAN TRADING SAHAM DIGITAL", 100));
            writer.println("═".repeat(100));
            writer.println();
            
            // Info Akun
            writer.println("INFORMASI AKUN:");
            writer.println("─".repeat(100));
            writer.println("Username        : " + akun.getUsername());
            writer.println("Nama Lengkap    : " + akun.getNamaLengkap());
            writer.println("Email           : " + akun.getEmail());
            writer.println("Saldo Cash      : Rp " + String.format("%,15.2f", akun.getSaldo()));
            
            // Portfolio
            writer.println("\n\nPORTFOLIO SAHAM:");
            writer.println("─".repeat(100));
            writer.printf("%-10s %-30s %-10s %-15s %-15s %-15s %-15s\n",
                "Kode", "Nama Saham", "Jumlah", "Harga Beli", "Harga Skrg", "Nilai", "Profit/Loss");
            writer.println("─".repeat(100));
            
            double totalNilai = akun.getSaldo();
            double totalKeuntungan = 0;
            
            for (Portfolio port : akun.getPortfolio().values()) {
                try {
                    Saham saham = pasar.getSaham(port.getKodeSaham());
                    double nilaiSkrg = port.hitungNilaiSekarang(saham.getHargaSekarang());
                    double profit = port.hitungKeuntungan(saham.getHargaSekarang());
                    
                    writer.printf("%-10s %-30s %,10d Rp %,12.2f Rp %,12.2f Rp %,12.2f %s Rp %,12.2f (%.2f%%)\n",
                        port.getKodeSaham(),
                        port.getNamaSaham(),
                        port.getJumlah(),
                        port.getHargaBeli(),
                        saham.getHargaSekarang(),
                        nilaiSkrg,
                        profit >= 0 ? "+" : "",
                        profit,
                        port.hitungPersentaseKeuntungan(saham.getHargaSekarang())
                    );
                    
                    totalNilai += nilaiSkrg;
                    totalKeuntungan += profit;
                } catch (SahamTidakDitemukanException e) {
                    writer.println("Error: " + e.getMessage());
                }
            }
            
            writer.println("─".repeat(100));
            writer.printf("TOTAL NILAI PORTFOLIO: Rp %,15.2f\n", totalNilai);
            writer.printf("TOTAL KEUNTUNGAN     : Rp %,15.2f\n", totalKeuntungan);
            
            // Riwayat Transaksi
            writer.println("\n\nRIWAYAT TRANSAKSI:");
            writer.println("─".repeat(100));
            
            java.util.ArrayList<Transaksi> riwayat = akun.getRiwayatTransaksi();
            for (int i = riwayat.size() - 1; i >= 0 && i >= riwayat.size() - 20; i--) {
                writer.println(riwayat.get(i));
            }
            
            writer.println("\n" + "═".repeat(100));
            writer.println(UIHelper.centerText("Laporan dibuat pada: " + 
                java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), 100));
            writer.println("═".repeat(100));
            
            System.out.println("\n✓ Laporan berhasil diekspor ke: " + filename);
            
        } catch (java.io.IOException e) {
            System.out.println("✗ Gagal membuat laporan: " + e.getMessage());
        }
    }
}