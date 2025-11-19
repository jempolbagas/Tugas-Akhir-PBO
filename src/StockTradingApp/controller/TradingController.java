package StockTradingApp.controller;

import StockTradingApp.*;
import StockTradingApp.view.ConsoleView;

import java.util.Scanner;

public class TradingController {
    private final ConsoleView view;
    private final SistemAutentikasi auth;
    private final PasarSaham pasar;
    private Akun akunAktif;
    private final Scanner scanner;

    public TradingController(ConsoleView view, SistemAutentikasi auth, PasarSaham pasar) {
        this.view = view;
        this.auth = auth;
        this.pasar = pasar;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        // Thread for automatic price updates every 10 seconds
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000); // 10 seconds
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

        view.tampilkanSplashScreen();
        scanner.nextLine();

        boolean running = true;
        while (running) {
            if (akunAktif == null) {
                running = menuUtama();
            } else {
                running = menuTrading();
            }
        }

        auth.saveData();
        view.exitMessage();
        scanner.close();
    }

    private boolean menuUtama() {
        view.menuUtama();
        try {
            int pilihan = Integer.parseInt(scanner.nextLine());

            switch (pilihan) {
                case 1:
                    buatAkunBaru();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    lihatHargaSahamGuest();
                    break;
                case 4:
                    return false;
                default:
                    view.displayError("Pilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            view.displayError("Input harus berupa angka!");
        }
        pause();
        return true;
    }

    private boolean menuTrading() {
        view.menuTrading(akunAktif, pasar.isPasarBuka());
        try {
            int pilihan = Integer.parseInt(scanner.nextLine());
            switch (pilihan) {
                case 1:
                    lihatDaftarSaham();
                    break;
                case 2:
                    beliSaham();
                    break;
                case 3:
                    jualSaham();
                    break;
                case 4:
                    lihatPortfolio();
                    break;
                case 5:
                    lihatRiwayatTransaksi();
                    break;
                case 6:
                    topUpSaldo();
                    break;
                case 7:
                    exportLaporan();
                    break;
                case 8:
                    pengaturanAkun();
                    break;
                case 9:
                    logout();
                    break;
                default:
                    view.displayError("Pilihan tidak valid!");
            }
        } catch (NumberFormatException e) {
            view.displayError("Input harus berupa angka!");
        }
        if (akunAktif != null) {
            pause();
        }
        return true;
    }

    private void buatAkunBaru() {
        view.headerBuatAkun();

        try {
            view.promptBuatAkunUsername();
            String username = scanner.nextLine();

            view.promptBuatAkunPassword();
            String password = scanner.nextLine();

            view.promptNamaLengkap();
            String namaLengkap = scanner.nextLine();

            view.promptEmail();
            String email = scanner.nextLine();

            view.promptDepositAwal();
            double depositAwal = Double.parseDouble(scanner.nextLine());

            if (depositAwal < 100000) {
                view.displayError("Deposit minimal Rp 100.000!");
                return;
            }

            auth.buatAkun(username, password, namaLengkap, email, depositAwal);
            view.successBuatAkun();

        } catch (Exception e) {
            view.displayError(e.getMessage());
        }
    }

    private void login() {
        view.headerLogin();
        try {
            view.promptUsername();
            String username = scanner.nextLine();

            view.promptPassword();
            String password = scanner.nextLine();

            akunAktif = auth.login(username, password);
            view.successLogin(akunAktif.getNamaLengkap());

        } catch (AkunTidakDitemukanException | PasswordSalahException e) {
            view.displayError(e.getMessage());
        }
    }

    private void lihatHargaSahamGuest() {
        view.lihatHargaSahamGuest(pasar.getAllSaham());
    }

    private void lihatDaftarSaham() {
        view.lihatDaftarSaham(pasar.getAllSaham());
    }

    private void beliSaham() {
        if (!pasar.isPasarBuka()) {
            view.displayError("⚠️  Pasar sedang tutup! Transaksi tidak dapat dilakukan.");
            return;
        }

        view.headerBeliSaham(akunAktif.getSaldo());

        try {
            view.promptKodeSaham();
            String kode = scanner.nextLine().toUpperCase();

            Saham saham = pasar.getSaham(kode);
            view.detailSaham(saham);

            view.promptBeliLot();
            int lot = Integer.parseInt(scanner.nextLine());

            if (lot <= 0) {
                view.displayError("Jumlah lot harus positif!");
                return;
            }

            int jumlahLembar = lot * 100;
            double totalHarga = saham.getHargaSekarang() * jumlahLembar;
            view.konfirmasiBeli(saham, lot, jumlahLembar, totalHarga, akunAktif.getSaldo());

            view.promptKonfirmasiBeli();
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("Y")) {
                akunAktif.beliSaham(saham, jumlahLembar);
                auth.saveData();
                view.successBeli(saham, jumlahLembar, akunAktif.getSaldo());
            } else {
                view.displayMessage("\nPembelian dibatalkan.");
            }

        } catch (SahamTidakDitemukanException | SaldoTidakCukupException e) {
            view.displayError(e.getMessage());
        } catch (NumberFormatException e) {
            view.displayError("Input tidak valid!");
        }
    }

    private void jualSaham() {
        if (!pasar.isPasarBuka()) {
            view.displayError("⚠️  Pasar sedang tutup! Transaksi tidak dapat dilakukan.");
            return;
        }
        if (akunAktif.getPortfolio().isEmpty()) {
            view.displayError("⚠️  Portfolio Anda kosong. Belum ada saham yang bisa dijual.");
            return;
        }

        view.headerJualSaham();
        view.portfolioJual(akunAktif.getPortfolio(), pasar);

        try {
            view.promptKodeSahamJual();
            String kode = scanner.nextLine().toUpperCase();

            Saham saham = pasar.getSaham(kode);
            Portfolio port = akunAktif.getPortfolio().get(kode);

            if (port == null) {
                view.displayError("Anda tidak memiliki saham " + kode);
                return;
            }

            view.detailJual(saham, port);

            view.promptJumlahJual(port.getJumlah());
            int jumlah = Integer.parseInt(scanner.nextLine());

            if (jumlah <= 0) {
                view.displayError("Jumlah harus positif!");
                return;
            }

            if (jumlah > port.getJumlah()) {
                view.displayError("Jumlah melebihi kepemilikan Anda!");
                return;
            }

            double totalPenjualan = saham.getHargaSekarang() * jumlah;
            double profit = (saham.getHargaSekarang() - port.getHargaBeli()) * jumlah;
            view.konfirmasiJual(saham, jumlah, totalPenjualan, profit);

            view.promptKonfirmasiJual();
            String konfirmasi = scanner.nextLine();

            if (konfirmasi.equalsIgnoreCase("Y")) {
                akunAktif.jualSaham(saham, jumlah);
                auth.saveData();
                view.successJual(saham, jumlah, akunAktif.getSaldo(), profit);
            } else {
                view.displayMessage("\nPenjualan dibatalkan.");
            }

        } catch (SahamTidakDitemukanException | JumlahSahamTidakValidException e) {
            view.displayError(e.getMessage());
        } catch (NumberFormatException e) {
            view.displayError("Input tidak valid!");
        }
    }

    private void lihatPortfolio() {
        view.lihatPortfolio(akunAktif, pasar);
    }

    private void lihatRiwayatTransaksi() {
        view.lihatRiwayatTransaksi(akunAktif.getRiwayatTransaksi());
    }

    private void topUpSaldo() {
        view.headerTopUp(akunAktif.getSaldo());

        try {
            view.promptTopUp();
            double jumlah = Double.parseDouble(scanner.nextLine());

            if (jumlah <= 0) {
                view.displayError("Jumlah harus positif!");
                return;
            }

            akunAktif.tambahSaldo(jumlah);
            auth.saveData();
            view.successTopUp(jumlah, akunAktif.getSaldo());

        } catch (NumberFormatException e) {
            view.displayError("Input tidak valid!");
        } catch (IllegalArgumentException e) {
            view.displayError(e.getMessage());
        }
    }

    private void exportLaporan() {
        view.exportLaporan();
        LaporanManager.exportLaporan(akunAktif, pasar);
    }

    private void pengaturanAkun() {
        view.pengaturanAkun(akunAktif);
    }

    private void logout() {
        view.logout(akunAktif.getNamaLengkap());
        akunAktif = null;
    }

    private void pause() {
        view.pause();
        scanner.nextLine();
    }
}
