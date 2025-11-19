package StockTradingApp.view;

import StockTradingApp.*;

import java.util.ArrayList;
import java.util.Map;

public class ConsoleView {

    public void tampilkanSplashScreen() {
        clearScreen();
        System.out.println("\n");
        System.out.println("  ███████╗████████╗ ██████╗  ██████╗██╗  ██╗    ████████╗██████╗  █████╗ ██████╗ ██╗███╗   ██╗ ██████╗ ");
        System.out.println("  ██╔════╝╚══██╔══╝██╔═══██╗██╔════╝██║ ██╔╝    ╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗██║████╗  ██║██╔════╝ ");
        System.out.println("  ███████╗   ██║   ██║   ██║██║     █████╔╝        ██║   ██████╔╝███████║██║  ██║██║██╔██╗ ██║██║  ███╗");
        System.out.println("  ╚════██║   ██║   ██║   ██║██║     ██╔═██╗        ██║   ██╔══██╗██╔══██║██║  ██║██║██║╚██╗██║██║   ██║");
        System.out.println("  ███████║   ██║   ╚██████╔╝╚██████╗██║  ██╗       ██║   ██║  ██║██║  ██║██████╔╝██║██║ ╚████║╚██████╔╝");
        System.out.println("  ╚══════╝   ╚═╝    ╚═════╝  ╚═════╝╚═╝  ╚═╝       ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚═╝╚═╝  ╚═══╝ ╚═════╝ ");
        System.out.println("\n                           🏆 Platform Trading Saham Digital Terpercaya 🏆");
        System.out.println("\n                                  [ Tekan ENTER untuk mulai ]");
    }

    public int menuUtama() {
        clearScreen();
        tampilkanHeader("SISTEM TRADING SAHAM DIGITAL");
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. 🆕 Buat Akun Baru                                                          │");
        System.out.println("│  2. 🔐 Login                                                                   │");
        System.out.println("│  3. 📊 Lihat Harga Saham (Guest)                                               │");
        System.out.println("│  4. ❌ Keluar                                                                   │");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
        System.out.print("\nPilih menu: ");
        return 0;
    }

    public void headerBuatAkun() {
        tampilkanHeader("PENDAFTARAN AKUN BARU");
    }

    public void successBuatAkun() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ✓ AKUN BERHASIL DIBUAT!                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nSelamat! Akun Anda telah berhasil dibuat.");
        System.out.println("Silakan login untuk mulai trading.");
    }

    public void headerLogin() {
        tampilkanHeader("LOGIN");
    }

    public void successLogin(String nama) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ✓ LOGIN BERHASIL!                                         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nSelamat datang kembali, " + nama + "!");
    }

    public void lihatHargaSahamGuest(java.util.Collection<Saham> values) {
        tampilkanHeader("DAFTAR HARGA SAHAM (REAL-TIME)");

        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-25s %-15s %-12s %-8s %-12s │\n",
                "Kode", "Nama Saham", "Sektor", "Harga", "Status", "Perubahan");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");

        for (Saham saham : values) {
            System.out.printf("│ %-8s %-25s %-15s Rp %,10.2f %s %-12s │\n",
                    saham.getKode(),
                    saham.getNamaSaham(),
                    saham.getSektor(),
                    saham.getHargaSekarang(),
                    saham.getStatusWarna(),
                    saham.getPerubahanFormatted());
        }
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");

        System.out.println("\n💡 Silakan login untuk mulai trading!");
    }

    public int menuTrading(Akun akunAktif, boolean pasarBuka) {
        clearScreen();
        tampilkanHeader("MENU TRADING - " + akunAktif.getNamaLengkap());

        // Tampilkan info singkat
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ 👤 %-20s │ 💰 Saldo: Rp %,20.2f │ 📊 Pasar: %-10s │\n",
                akunAktif.getUsername(),
                akunAktif.getSaldo(),
                pasarBuka ? "BUKA 🟢" : "TUTUP 🔴");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");

        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. 📈 Lihat Daftar Saham & Harga Real-Time                                    │");
        System.out.println("│  2. 💵 Beli Saham                                                               │");
        System.out.println("│  3. 💸 Jual Saham                                                               │");
        System.out.println("│  4. 📊 Lihat Portfolio                                                          │");
        System.out.println("│  5. 📜 Riwayat Transaksi                                                        │");
        System.out.println("│  6. 💰 Top Up Saldo                                                             │");
        System.out.println("│  7. 📄 Export Laporan                                                           │");
        System.out.println("│  8. ⚙️  Pengaturan Akun                                                         │");
        System.out.println("│  9. 🚪 Logout                                                                   │");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
        System.out.print("\nPilih menu: ");
        return 0;
    }

    public void lihatDaftarSaham(java.util.Collection<Saham> values) {
        tampilkanHeader("DAFTAR SAHAM - REAL TIME UPDATE");

        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-25s %-15s %-12s %-8s %-12s │\n",
                "Kode", "Nama Saham", "Sektor", "Harga", "Status", "Perubahan");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");

        for (Saham saham : values) {
            System.out.printf("│ %-8s %-25s %-15s Rp %,10.2f %s %-12s │\n",
                    saham.getKode(),
                    saham.getNamaSaham(),
                    saham.getSektor(),
                    saham.getHargaSekarang(),
                    saham.getStatusWarna(),
                    saham.getPerubahanFormatted());
        }
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");

        System.out.println("\n💡 Harga diperbarui otomatis setiap 10 detik");
        System.out.println("🟢 = Naik | 🔴 = Turun | ⚪ = Stabil");
    }

    public void headerBeliSaham(double saldo) {
        tampilkanHeader("BELI SAHAM");
        System.out.println("\nSaldo Anda: Rp " + String.format("%,15.2f", saldo));
    }

    public void detailSaham(Saham saham) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Detail Saham:");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Kode         : " + saham.getKode());
        System.out.println("│ Nama         : " + saham.getNamaSaham());
        System.out.println("│ Sektor       : " + saham.getSektor());
        System.out.println("│ Harga        : Rp " + String.format("%,12.2f", saham.getHargaSekarang()));
        System.out.println("│ Perubahan    : " + saham.getStatusWarna() + " " + saham.getPerubahanFormatted());
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void konfirmasiBeli(Saham saham, int lot, int jumlahLembar, double totalHarga, double saldo) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ KONFIRMASI PEMBELIAN");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ Saham           : %s - %s\n", saham.getKode(), saham.getNamaSaham());
        System.out.printf("│ Jumlah          : %d lot (%,d lembar)\n", lot, jumlahLembar);
        System.out.printf("│ Harga per lembar: Rp %,12.2f\n", saham.getHargaSekarang());
        System.out.printf("│ Total Bayar     : Rp %,12.2f\n", totalHarga);
        System.out.printf("│ Saldo Anda      : Rp %,12.2f\n", saldo);
        System.out.printf("│ Sisa Saldo      : Rp %,12.2f\n", saldo - totalHarga);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void successBeli(Saham saham, int jumlahLembar, double saldo) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ✓ PEMBELIAN BERHASIL!                                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nAnda telah membeli " + jumlahLembar + " lembar saham " + saham.getKode());
        System.out.println("Saldo tersisa: Rp " + String.format("%,15.2f", saldo));
    }

    public void headerJualSaham() {
        tampilkanHeader("JUAL SAHAM");
    }

    public void portfolioJual(Map<String, Portfolio> portfolioMap, PasarSaham pasar) {
        System.out.println("\nPortfolio Anda:");
        System.out.println("┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-30s %-10s %-15s %-15s │\n",
                "Kode", "Nama Saham", "Jumlah", "Harga Beli", "Harga Sekarang");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");

        for (Portfolio port : portfolioMap.values()) {
            try {
                Saham saham = pasar.getSaham(port.getKodeSaham());
                System.out.printf("│ %-8s %-30s %,10d Rp %,12.2f Rp %,12.2f │\n",
                        port.getKodeSaham(),
                        port.getNamaSaham(),
                        port.getJumlah(),
                        port.getHargaBeli(),
                        saham.getHargaSekarang());
            } catch (SahamTidakDitemukanException e) {
                System.out.println("│ Error: " + e.getMessage());
            }
        }
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void detailJual(Saham saham, Portfolio port) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ Detail Kepemilikan:");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Kode         : " + saham.getKode());
        System.out.println("│ Nama         : " + saham.getNamaSaham());
        System.out.println("│ Kepemilikan  : " + String.format("%,d", port.getJumlah()) + " lembar");
        System.out.println("│ Harga Beli   : Rp " + String.format("%,12.2f", port.getHargaBeli()));
        System.out.println("│ Harga Jual   : Rp " + String.format("%,12.2f", saham.getHargaSekarang()));
        System.out.printf("│ Profit/Loss  : %s Rp %,12.2f (%.2f%%)\n",
                port.hitungKeuntungan(saham.getHargaSekarang()) >= 0 ? "+" : "",
                port.hitungKeuntungan(saham.getHargaSekarang()),
                port.hitungPersentaseKeuntungan(saham.getHargaSekarang()));
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void konfirmasiJual(Saham saham, int jumlah, double totalPenjualan, double profit) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ KONFIRMASI PENJUALAN");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ Saham           : %s - %s\n", saham.getKode(), saham.getNamaSaham());
        System.out.printf("│ Jumlah          : %,d lembar\n", jumlah);
        System.out.printf("│ Harga per lembar: Rp %,12.2f\n", saham.getHargaSekarang());
        System.out.printf("│ Total Terima    : Rp %,12.2f\n", totalPenjualan);
        System.out.printf("│ Profit/Loss     : %s Rp %,12.2f\n", profit >= 0 ? "+" : "", profit);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void successJual(Saham saham, int jumlah, double saldo, double profit) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ✓ PENJUALAN BERHASIL!                                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nAnda telah menjual " + jumlah + " lembar saham " + saham.getKode());
        System.out.println("Saldo Anda sekarang: Rp " + String.format("%,15.2f", saldo));

        if (profit >= 0) {
            System.out.println("🎉 Selamat! Anda mendapat profit: Rp " + String.format("%,12.2f", profit));
        } else {
            System.out.println("📉 Anda mengalami loss: Rp " + String.format("%,12.2f", Math.abs(profit)));
        }
    }

    public void lihatPortfolio(Akun akunAktif, PasarSaham pasar) {
        tampilkanHeader("PORTFOLIO SAHAM");

        if (akunAktif.getPortfolio().isEmpty()) {
            System.out.println("\n⚠️  Portfolio Anda masih kosong.");
            System.out.println("💡 Mulai investasi dengan membeli saham!");
            return;
        }

        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-20s %10s %13s %13s %13s %15s │\n",
                "Kode", "Nama Saham", "Jumlah", "Harga Beli", "Harga Skrg", "Nilai", "Profit/Loss");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");

        double totalModal = 0;
        double totalNilai = 0;

        for (Portfolio port : akunAktif.getPortfolio().values()) {
            try {
                Saham saham = pasar.getSaham(port.getKodeSaham());
                double nilaiSkrg = port.hitungNilaiSekarang(saham.getHargaSekarang());
                double profit = port.hitungKeuntungan(saham.getHargaSekarang());
                double persentase = port.hitungPersentaseKeuntungan(saham.getHargaSekarang());

                System.out.printf("│ %-8s %-20s %,10d Rp %,10.2f Rp %,10.2f Rp %,10.2f %s%,10.2f │\n",
                        port.getKodeSaham(),
                        port.getNamaSaham().length() > 20 ?
                                port.getNamaSaham().substring(0, 17) + "..." : port.getNamaSaham(),
                        port.getJumlah(),
                        port.getHargaBeli(),
                        saham.getHargaSekarang(),
                        nilaiSkrg,
                        profit >= 0 ? "+" : "",
                        profit);
                System.out.printf("│          (%.2f%%)                                                                          │\n",
                        persentase);

                totalModal += port.getTotalModal();
                totalNilai += nilaiSkrg;

            } catch (SahamTidakDitemukanException e) {
                System.out.println("│ Error: " + e.getMessage());
            }
        }

        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ %-50s Rp %,15.2f │\n", "TOTAL MODAL INVESTASI:", totalModal);
        System.out.printf("│ %-50s Rp %,15.2f │\n", "TOTAL NILAI PORTFOLIO:", totalNilai);
        System.out.printf("│ %-50s Rp %,15.2f │\n", "SALDO CASH:", akunAktif.getSaldo());
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");

        double totalAset = totalNilai + akunAktif.getSaldo();
        double totalProfit = totalNilai - totalModal;
        double persentaseProfit = totalModal > 0 ? (totalProfit / totalModal) * 100 : 0;

        System.out.printf("│ %-50s Rp %,15.2f │\n", "TOTAL ASET:", totalAset);
        System.out.printf("│ %-50s %s Rp %,12.2f (%.2f%%) │\n",
                "TOTAL PROFIT/LOSS:",
                totalProfit >= 0 ? "+" : "",
                totalProfit,
                persentaseProfit);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void lihatRiwayatTransaksi(ArrayList<Transaksi> riwayat) {
        tampilkanHeader("RIWAYAT TRANSAKSI");

        if (riwayat.isEmpty()) {
            System.out.println("\n⚠️  Belum ada riwayat transaksi.");
            return;
        }

        System.out.println("\nTotal Transaksi: " + riwayat.size());
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");

        // Tampilkan 20 transaksi terakhir
        int batas = Math.min(20, riwayat.size());
        for (int i = riwayat.size() - 1; i >= riwayat.size() - batas; i--) {
            System.out.println("│ " + riwayat.get(i).toString().substring(0,
                    Math.min(78, riwayat.get(i).toString().length())) + " │");
        }

        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");

        if (riwayat.size() > 20) {
            System.out.println("\n💡 Menampilkan 20 transaksi terakhir. Gunakan fitur export untuk melihat semua.");
        }
    }

    public void headerTopUp(double saldo) {
        tampilkanHeader("TOP UP SALDO");
        System.out.println("\nSaldo Anda saat ini: Rp " + String.format("%,15.2f", saldo));
    }

    public void successTopUp(double jumlah, double saldo) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     ✓ TOP UP BERHASIL!                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nJumlah top up  : Rp " + String.format("%,15.2f", jumlah));
        System.out.println("Saldo sekarang : Rp " + String.format("%,15.2f", saldo));
    }

    public void exportLaporan() {
        tampilkanHeader("EXPORT LAPORAN");
        System.out.println("\n📄 Membuat laporan trading...");
        System.out.println("\n💡 Laporan berisi:");
        System.out.println("   • Informasi akun lengkap");
        System.out.println("   • Detail portfolio dan profit/loss");
        System.out.println("   • Riwayat transaksi lengkap");
        System.out.println("   • Statistik trading");
    }

    public void pengaturanAkun(Akun akunAktif) {
        tampilkanHeader("PENGATURAN AKUN");
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss");

        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ INFORMASI AKUN");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Username       : " + akunAktif.getUsername());
        System.out.println("│ Nama Lengkap   : " + akunAktif.getNamaLengkap());
        System.out.println("│ Email          : " + akunAktif.getEmail());
        System.out.println("│ Tanggal Daftar : " + akunAktif.getTanggalBuat().format(formatter));
        System.out.println("│ Saldo          : Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
        System.out.println("│ Jumlah Saham   : " + akunAktif.getPortfolio().size() + " jenis saham");
        System.out.println("│ Total Transaksi: " + akunAktif.getRiwayatTransaksi().size() + " transaksi");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }

    public void logout(String nama) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     👋 LOGOUT BERHASIL                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nSampai jumpa, " + nama + "!");
    }

    public void exitMessage() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Terima kasih telah menggunakan Sistem Trading Saham Digital!           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝\n");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displayError(String message) {
        System.out.println("\n✗ " + message);
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private String centerText(String text, int width) {
        if (text.length() >= width) {
            return text;
        }
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append(" ");
        }
        sb.append(text);
        while (sb.length() < width) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public void tampilkanHeader(String judul) {
        clearScreen();
        int width = 82;
        String border = "╔" + "═".repeat(width - 2) + "╗";
        String empty = "║" + " ".repeat(width - 2) + "║";
        String title = "║" + centerText(judul, width - 2) + "║";

        System.out.println(border);
        System.out.println(empty);
        System.out.println(title);
        System.out.println(empty);
        System.out.println("╚" + "═".repeat(width - 2) + "╝");
    }

    public void pause() {
        System.out.print("\nTekan ENTER untuk kembali ke menu...");
    }

    public void promptUsername() {
        System.out.print("\nUsername: ");
    }

    public void promptPassword() {
        System.out.print("Password: ");
    }

    public void promptBuatAkunUsername() {
        System.out.print("\nUsername (min. 4 karakter)    : ");
    }

    public void promptBuatAkunPassword() {
        System.out.print("Password (min. 6 karakter)    : ");
    }

    public void promptNamaLengkap() {
        System.out.print("Nama Lengkap                  : ");
    }

    public void promptEmail() {
        System.out.print("Email                         : ");
    }

    public void promptDepositAwal() {
        System.out.print("Deposit Awal (min. Rp 100.000): Rp ");
    }

    public void promptKodeSaham() {
        System.out.print("\nMasukkan kode saham: ");
    }

    public void promptBeliLot() {
        System.out.print("\nJumlah lot yang ingin dibeli (1 lot = 100 lembar): ");
    }

    public void promptKonfirmasiBeli() {
        System.out.print("\nLanjutkan pembelian? (Y/N): ");
    }

    public void promptKodeSahamJual() {
        System.out.print("\nMasukkan kode saham yang ingin dijual: ");
    }

    public void promptJumlahJual(int max) {
        System.out.print("\nJumlah lembar yang ingin dijual (max " + String.format("%,d", max) + "): ");
    }

    public void promptKonfirmasiJual() {
        System.out.print("\nLanjutkan penjualan? (Y/N): ");
    }

    public void promptTopUp() {
        System.out.print("\nMasukkan jumlah top up: Rp ");
    }
}
