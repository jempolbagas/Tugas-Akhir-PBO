package StockTradingApp;

public class SistemTradingSaham {
    private static java.util.Scanner scanner = new java.util.Scanner(System.in);
    private static SistemAutentikasi auth;
    private static PasarSaham pasar = new PasarSaham();
    private static Akun akunAktif = null;
    
    public static void main(String[] args) {
        try {
            auth = new SistemAutentikasi();
            java.util.List<String> notifications = auth.getNotifications();
            if (!notifications.isEmpty()) {
                for (String notification : notifications) {
                    UIHelper.showNotification(notification);
                }
                UIHelper.pause();
            }
        } catch (DatabaseLoadException | DatabaseSaveException e) {
            UIHelper.showErrorAndExit("Gagal memuat data penting.", e);
            return; // Exit if auth fails
        }

        // Thread untuk update harga otomatis setiap 10 detik
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000); // 10 detik
                    if (pasar.isPasarBuka()) {
                        pasar.updateHargaSemua();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.setDaemon(true);
        updateThread.start();
        
        tampilkanSplashScreen();
        
        boolean running = true;
        while (running) {
            if (akunAktif == null) {
                running = menuUtama();
            } else {
                running = menuTrading();
            }
        }
        
        try {
            auth.saveData();
        } catch (DatabaseSaveException e) {
            UIHelper.showErrorAndExit("Gagal menyimpan data saat keluar.", e);
        }
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        Terima kasih telah menggunakan Sistem Trading Saham Digital!           ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝\n");
        scanner.close();
    }
    
    private static void tampilkanSplashScreen() {
        UIHelper.clearScreen();
        System.out.println("\n");
        System.out.println("  ███████╗████████╗ ██████╗  ██████╗██╗  ██╗    ████████╗██████╗  █████╗ ██████╗ ██╗███╗   ██╗ ██████╗ ");
        System.out.println("  ██╔════╝╚══██╔══╝██╔═══██╗██╔════╝██║ ██╔╝    ╚══██╔══╝██╔══██╗██╔══██╗██╔══██╗██║████╗  ██║██╔════╝ ");
        System.out.println("  ███████╗   ██║   ██║   ██║██║     █████╔╝        ██║   ██████╔╝███████║██║  ██║██║██╔██╗ ██║██║  ███╗");
        System.out.println("  ╚════██║   ██║   ██║   ██║██║     ██╔═██╗        ██║   ██╔══██╗██╔══██║██║  ██║██║██║╚██╗██║██║   ██║");
        System.out.println("  ███████║   ██║   ╚██████╔╝╚██████╗██║  ██╗       ██║   ██║  ██║██║  ██║██████╔╝██║██║ ╚████║╚██████╔╝");
        System.out.println("  ╚══════╝   ╚═╝    ╚═════╝  ╚═════╝╚═╝  ╚═╝       ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚═╝╚═╝  ╚═══╝ ╚═════╝ ");
        System.out.println("\n                           🏆 Platform Trading Saham Digital Terpercaya 🏆");
        System.out.println("\n                                  [ Tekan ENTER untuk mulai ]");
        scanner.nextLine();
    }
    
    private static boolean menuUtama() {
        UIHelper.clearScreen();
        UIHelper.tampilkanHeader("SISTEM TRADING SAHAM DIGITAL");
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│  1. 🆕 Buat Akun Baru                                                          │");
        System.out.println("│  2. 🔐 Login                                                                   │");
        System.out.println("│  3. 📊 Lihat Harga Saham (Guest)                                               │");
        System.out.println("│  4. ❌ Keluar                                                                   │");
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
        System.out.print("\nPilih menu: ");
        
        try {
            int pilihan = Integer.parseInt(scanner.nextLine());
            
            switch (pilihan) {
                case 1: buatAkunBaru(); break;
                case 2: login(); break;
                case 3: lihatHargaSahamGuest(); break;
                case 4: return false;
                default: System.out.println("Pilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input harus berupa angka!");
        }
        
        UIHelper.pause();
        return true;
    }
    
    private static void buatAkunBaru() {
        UIHelper.tampilkanHeader("PENDAFTARAN AKUN BARU");
        
        try {
            System.out.print("\nUsername (min. 4 karakter)    : ");
            String username = scanner.nextLine();
            
            System.out.print("Password (min. 6 karakter)    : ");
            String password = scanner.nextLine();
            
            System.out.print("Nama Lengkap                  : ");
            String namaLengkap = scanner.nextLine();
            
            System.out.print("Email                         : ");
            String email = scanner.nextLine();
            
            System.out.print("Deposit Awal (min. Rp 100.000): Rp ");
            double depositAwal = Double.parseDouble(scanner.nextLine());
            
            if (depositAwal < 100000) {
                System.out.println("\n✗ Deposit minimal Rp 100.000!");
                return;
            }
            
            auth.buatAkun(username, password, namaLengkap, email, depositAwal);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ✓ AKUN BERHASIL DIBUAT!                                    ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nSelamat! Akun Anda telah berhasil dibuat.");
            System.out.println("Silakan login untuk mulai trading.");
            
        } catch (DatabaseSaveException e) {
            System.out.println("\n✗ Gagal menyimpan akun baru: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }
    
    private static void login() {
        UIHelper.tampilkanHeader("LOGIN");
        
        try {
            System.out.print("\nUsername: ");
            String username = scanner.nextLine();
            
            System.out.print("Password: ");
            String password = scanner.nextLine();
            
            akunAktif = auth.login(username, password);
            
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ✓ LOGIN BERHASIL!                                         ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nSelamat datang kembali, " + akunAktif.getNamaLengkap() + "!");
            
        } catch (AkunTidakDitemukanException | PasswordSalahException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }
    
    private static void lihatHargaSahamGuest() {
        UIHelper.tampilkanHeader("DAFTAR HARGA SAHAM (REAL-TIME)");
        
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-25s %-15s %-12s %-8s %-12s │\n",
            "Kode", "Nama Saham", "Sektor", "Harga", "Status", "Perubahan");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        
        for (Saham saham : pasar.getAllSaham()) {
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
    
    private static boolean menuTrading() {
        UIHelper.clearScreen();
        UIHelper.tampilkanHeader("MENU TRADING - " + akunAktif.getNamaLengkap());
        
        // Tampilkan info singkat
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ 👤 %-20s │ 💰 Saldo: Rp %,20.2f │ 📊 Pasar: %-10s │\n",
            akunAktif.getUsername(),
            akunAktif.getSaldo(),
            pasar.isPasarBuka() ? "BUKA 🟢" : "TUTUP 🔴");
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
        
        try {
            int pilihan = Integer.parseInt(scanner.nextLine());
            
            switch (pilihan) {
                case 1: lihatDaftarSaham(); break;
                case 2: beliSaham(); break;
                case 3: jualSaham(); break;
                case 4: lihatPortfolio(); break;
                case 5: lihatRiwayatTransaksi(); break;
                case 6: topUpSaldo(); break;
                case 7: exportLaporan(); break;
                case 8: pengaturanAkun(); break;
                case 9: logout(); break;
                default: System.out.println("Pilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input harus berupa angka!");
        }
        
        if (akunAktif != null) {
            UIHelper.pause();
        }
        return true;
    }
    
    private static void lihatDaftarSaham() {
        UIHelper.tampilkanHeader("DAFTAR SAHAM - REAL TIME UPDATE");
        
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-25s %-15s %-12s %-8s %-12s │\n",
            "Kode", "Nama Saham", "Sektor", "Harga", "Status", "Perubahan");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        
        for (Saham saham : pasar.getAllSaham()) {
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
    
    private static void beliSaham() {
        UIHelper.tampilkanHeader("BELI SAHAM");
        
        if (!pasar.isPasarBuka()) {
            System.out.println("\n⚠️  Pasar sedang tutup! Transaksi tidak dapat dilakukan.");
            return;
        }
        
        System.out.println("\nSaldo Anda: Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
        
        try {
            System.out.print("\nMasukkan kode saham: ");
            String kode = scanner.nextLine().toUpperCase();
            
            Saham saham = pasar.getSaham(kode);
            
            System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
            System.out.println("│ Detail Saham:");
            System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
            System.out.println("│ Kode         : " + saham.getKode());
            System.out.println("│ Nama         : " + saham.getNamaSaham());
            System.out.println("│ Sektor       : " + saham.getSektor());
            System.out.println("│ Harga        : Rp " + String.format("%,12.2f", saham.getHargaSekarang()));
            System.out.println("│ Perubahan    : " + saham.getStatusWarna() + " " + saham.getPerubahanFormatted());
            System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
            
            System.out.print("\nJumlah lot yang ingin dibeli (1 lot = 100 lembar): ");
            int lot = Integer.parseInt(scanner.nextLine());
            
            if (lot <= 0) {
                System.out.println("\n✗ Jumlah lot harus positif!");
                return;
            }
            
            int jumlahLembar = lot * 100;
            double totalHarga = saham.getHargaSekarang() * jumlahLembar;
            
            System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
            System.out.println("│ KONFIRMASI PEMBELIAN");
            System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
            System.out.printf("│ Saham           : %s - %s\n", saham.getKode(), saham.getNamaSaham());
            System.out.printf("│ Jumlah          : %d lot (%,d lembar)\n", lot, jumlahLembar);
            System.out.printf("│ Harga per lembar: Rp %,12.2f\n", saham.getHargaSekarang());
            System.out.printf("│ Total Bayar     : Rp %,12.2f\n", totalHarga);
            System.out.printf("│ Saldo Anda      : Rp %,12.2f\n", akunAktif.getSaldo());
            System.out.printf("│ Sisa Saldo      : Rp %,12.2f\n", akunAktif.getSaldo() - totalHarga);
            System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
            
            System.out.print("\nLanjutkan pembelian? (Y/N): ");
            String konfirmasi = scanner.nextLine();
            
            if (konfirmasi.equalsIgnoreCase("Y")) {
                akunAktif.beliSaham(saham, jumlahLembar);
                try {
                    auth.saveData();
                } catch (DatabaseSaveException e) {
                    System.out.println("\n✗ Gagal menyimpan transaksi: " + e.getMessage());
                    akunAktif.rollbackLastTransaction();
                }
                System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
                System.out.println("║                     ✓ PEMBELIAN BERHASIL!                                     ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
                System.out.println("\nAnda telah membeli " + jumlahLembar + " lembar saham " + saham.getKode());
                System.out.println("Saldo tersisa: Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
            } else {
                System.out.println("\n✗ Pembelian dibatalkan.");
            }
            
        } catch (SahamTidakDitemukanException | SaldoTidakCukupException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Input tidak valid!");
        }
    }
    
    private static void jualSaham() {
        UIHelper.tampilkanHeader("JUAL SAHAM");
        
        if (!pasar.isPasarBuka()) {
            System.out.println("\n⚠️  Pasar sedang tutup! Transaksi tidak dapat dilakukan.");
            return;
        }
        
        if (akunAktif.getPortfolio().isEmpty()) {
            System.out.println("\n⚠️  Portfolio Anda kosong. Belum ada saham yang bisa dijual.");
            return;
        }
        
        // Tampilkan portfolio
        System.out.println("\nPortfolio Anda:");
        System.out.println("┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ %-8s %-30s %-10s %-15s %-15s │\n",
            "Kode", "Nama Saham", "Jumlah", "Harga Beli", "Harga Sekarang");
        System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
        
        for (Portfolio port : akunAktif.getPortfolio().values()) {
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
        
        try {
            System.out.print("\nMasukkan kode saham yang ingin dijual: ");
            String kode = scanner.nextLine().toUpperCase();
            
            Saham saham = pasar.getSaham(kode);
            Portfolio port = akunAktif.getPortfolio().get(kode);
            
            if (port == null) {
                System.out.println("\n✗ Anda tidak memiliki saham " + kode);
                return;
            }
            
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
            
            System.out.print("\nJumlah lembar yang ingin dijual (max " + 
                String.format("%,d", port.getJumlah()) + "): ");
            int jumlah = Integer.parseInt(scanner.nextLine());
            
            if (jumlah <= 0) {
                System.out.println("\n✗ Jumlah harus positif!");
                return;
            }
            
            if (jumlah > port.getJumlah()) {
                System.out.println("\n✗ Jumlah melebihi kepemilikan Anda!");
                return;
            }
            
            double totalPenjualan = saham.getHargaSekarang() * jumlah;
            double profit = (saham.getHargaSekarang() - port.getHargaBeli()) * jumlah;
            
            System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
            System.out.println("│ KONFIRMASI PENJUALAN");
            System.out.println("├────────────────────────────────────────────────────────────────────────────────┤");
            System.out.printf("│ Saham           : %s - %s\n", saham.getKode(), saham.getNamaSaham());
            System.out.printf("│ Jumlah          : %,d lembar\n", jumlah);
            System.out.printf("│ Harga per lembar: Rp %,12.2f\n", saham.getHargaSekarang());
            System.out.printf("│ Total Terima    : Rp %,12.2f\n", totalPenjualan);
            System.out.printf("│ Profit/Loss     : %s Rp %,12.2f\n", profit >= 0 ? "+" : "", profit);
            System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
            
            System.out.print("\nLanjutkan penjualan? (Y/N): ");
            String konfirmasi = scanner.nextLine();
            
            if (konfirmasi.equalsIgnoreCase("Y")) {
                akunAktif.jualSaham(saham, jumlah);
                try {
                    auth.saveData();
                } catch (DatabaseSaveException e) {
                    System.out.println("\n✗ Gagal menyimpan transaksi: " + e.getMessage());
                    akunAktif.rollbackLastTransaction();
                }
                System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
                System.out.println("║                     ✓ PENJUALAN BERHASIL!                                     ║");
                System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
                System.out.println("\nAnda telah menjual " + jumlah + " lembar saham " + saham.getKode());
                System.out.println("Saldo Anda sekarang: Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
                
                if (profit >= 0) {
                    System.out.println("🎉 Selamat! Anda mendapat profit: Rp " + String.format("%,12.2f", profit));
                } else {
                    System.out.println("📉 Anda mengalami loss: Rp " + String.format("%,12.2f", Math.abs(profit)));
                }
            } else {
                System.out.println("\n✗ Penjualan dibatalkan.");
            }
            
        } catch (SahamTidakDitemukanException | JumlahSahamTidakValidException e) {
            System.out.println("\n✗ " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Input tidak valid!");
        }
    }
    
    private static void lihatPortfolio() {
        UIHelper.tampilkanHeader("PORTFOLIO SAHAM");
        
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
    
    private static void lihatRiwayatTransaksi() {
        UIHelper.tampilkanHeader("RIWAYAT TRANSAKSI");
        
        java.util.ArrayList<Transaksi> riwayat = akunAktif.getRiwayatTransaksi();
        
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
    
    private static void topUpSaldo() {
        UIHelper.tampilkanHeader("TOP UP SALDO");
        
        System.out.println("\nSaldo Anda saat ini: Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
        
        try {
            System.out.print("\nMasukkan jumlah top up: Rp ");
            double jumlah = Double.parseDouble(scanner.nextLine());
            
            if (jumlah <= 0) {
                System.out.println("\n✗ Jumlah harus positif!");
                return;
            }
            
            akunAktif.tambahSaldo(jumlah);
            try {
                auth.saveData();
            } catch (DatabaseSaveException e) {
                System.out.println("\n✗ Gagal menyimpan transaksi: " + e.getMessage());
                akunAktif.rollbackLastTransaction();
            }
            System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ✓ TOP UP BERHASIL!                                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("\nJumlah top up  : Rp " + String.format("%,15.2f", jumlah));
            System.out.println("Saldo sekarang : Rp " + String.format("%,15.2f", akunAktif.getSaldo()));
            
        } catch (NumberFormatException e) {
            System.out.println("\n✗ Input tidak valid!");
        } catch (IllegalArgumentException e) {
            System.out.println("\n✗ " + e.getMessage());
        }
    }
    
    private static void exportLaporan() {
        UIHelper.tampilkanHeader("EXPORT LAPORAN");
        
        System.out.println("\n📄 Membuat laporan trading...");
        LaporanManager.exportLaporan(akunAktif, pasar);
        
        System.out.println("\n💡 Laporan berisi:");
        System.out.println("   • Informasi akun lengkap");
        System.out.println("   • Detail portfolio dan profit/loss");
        System.out.println("   • Riwayat transaksi lengkap");
        System.out.println("   • Statistik trading");
    }
    
    private static void pengaturanAkun() {
        UIHelper.tampilkanHeader("PENGATURAN AKUN");
        
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
    
    private static void logout() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     👋 LOGOUT BERHASIL                                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\nSampai jumpa, " + akunAktif.getNamaLengkap() + "!");
        akunAktif = null;
    }
}