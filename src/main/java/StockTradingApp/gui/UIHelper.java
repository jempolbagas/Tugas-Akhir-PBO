package main.java.StockTradingApp.gui;

import java.util.Scanner;

/**
 * Utility class for Console/CLI interface operations.
 * Provides methods for clearing screen, formatting text, and displaying headers.
 * Note: This is primarily for the legacy CLI mode.
 */
public class UIHelper {
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Clears the console screen using ANSI escape codes.
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    
    /**
     * Displays a formatted main header with the given title.
     *
     * @param judul The title text.
     */
    public static void tampilkanHeader(String judul) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║" + centerText(judul, 80) + "║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
    }
    
    /**
     * Displays a formatted sub-header.
     *
     * @param subjudul The subtitle text.
     */
    public static void tampilkanSubHeader(String subjudul) {
        System.out.println("\n┌────────────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│ " + subjudul);
        System.out.println("└────────────────────────────────────────────────────────────────────────────────┘");
    }
    
    /**
     * Centers text within a given width by padding with spaces.
     *
     * @param text  The text to center.
     * @param width The total width.
     * @return The centered text string.
     */
    public static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }
    
    /**
     * Pauses execution until the user presses ENTER.
     */
    public static void pause() {
        System.out.print("\n[Tekan ENTER untuk melanjutkan...]");
        if (scanner.hasNextLine()) scanner.nextLine();
    }

    /**
     * Displays a critical error message and terminates the application.
     *
     * @param message The error message.
     * @param e       The exception causing the error (can be null).
     */
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

    /**
     * Displays a notification message to the console.
     *
     * @param message The notification message.
     */
    public static void showNotification(String message) {
        System.out.println("\n=====================================");
        System.out.println("           PEMBERITAHUAN          ");
        System.out.println("=====================================");
        System.out.println(message);
        System.out.println("=====================================");
    }
}
