package main.java.StockTradingApp.gui;

import java.util.Scanner;

public class UIHelper {
    private static Scanner scanner = new Scanner(System.in);

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    public static void tampilkanHeader(String judul) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║" + centerText(judul, 80) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
    }
    
    public static void tampilkanSubHeader(String subjudul) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ " + subjudul);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }
    
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    public static void pause() {
        System.out.print("\n[Tekan ENTER untuk melanjutkan...]");
        if (scanner.hasNextLine()) scanner.nextLine();
    }

    public static void showErrorAndExit(String message, Exception e) {
        System.err.println("=====================================");
        System.err.println("           ERROR KRITIS          ");
        System.err.println("=====================================");
        System.err.println(message);
        if (e != null) {
            System.err.println("Detail error: " + e.getMessage());
        }
        System.err.println("Aplikasi akan ditutup.");
        System.err.println("=====================================");
        System.exit(1);
    }

    public static void showNotification(String message) {
        System.out.println("\n=====================================");
        System.out.println("           PEMBERITAHUAN          ");
        System.out.println("=====================================");
        System.out.println(message);
        System.out.println("=====================================");
    }
}
