package main.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.SahamTidakDitemukanException;
import main.java.StockTradingApp.gui.UIHelper;
import main.java.StockTradingApp.model.*;

import java.math.BigDecimal;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LaporanManager {
    public static void exportLaporan(Akun akun, PasarSaham pasar) {
        String filename = "Laporan_Trading_" + akun.getUsername() + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
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
            
            BigDecimal totalNilai = akun.getSaldo();
            BigDecimal totalKeuntungan = BigDecimal.ZERO;
            
            for (Portfolio port : akun.getPortfolio().values()) {
                try {
                    Saham saham = pasar.getSaham(port.getKodeSaham());
                    BigDecimal nilaiSkrg = port.hitungNilaiSekarang(saham.getHargaSekarang());
                    BigDecimal profit = port.hitungKeuntungan(saham.getHargaSekarang());
                    
                    writer.printf("%-10s %-30s %,10d Rp %,12.2f Rp %,12.2f Rp %,12.2f %s Rp %,12.2f (%.2f%%)\n",
                        port.getKodeSaham(),
                        port.getNamaSaham(),
                        port.getJumlah(),
                        port.getHargaBeli(),
                        saham.getHargaSekarang(),
                        nilaiSkrg,
                        profit.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "",
                        profit,
                        port.hitungPersentaseKeuntungan(saham.getHargaSekarang())
                    );
                    
                    totalNilai = totalNilai.add(nilaiSkrg);
                    totalKeuntungan = totalKeuntungan.add(profit);
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
            
            ArrayList<Transaksi> riwayat = akun.getRiwayatTransaksi();
            for (int i = riwayat.size() - 1; i >= 0 && i >= riwayat.size() - 20; i--) {
                writer.println(riwayat.get(i));
            }
            
            writer.println("\n" + "═".repeat(100));
            writer.println(UIHelper.centerText("Laporan dibuat pada: " + 
                LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), 100));
            writer.println("═".repeat(100));
            
            System.out.println("\n✓ Laporan berhasil diekspor ke: " + filename);
            
        } catch (IOException e) {
            System.out.println("✗ Gagal membuat laporan: " + e.getMessage());
        }
    }
}
