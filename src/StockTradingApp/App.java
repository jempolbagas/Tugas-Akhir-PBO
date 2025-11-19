package StockTradingApp;

import StockTradingApp.controller.TradingController;
import StockTradingApp.view.ConsoleView;

public class App {
    public static void main(String[] args) {
        // Initialize Model
        SistemAutentikasi auth = new SistemAutentikasi();
        PasarSaham pasar = new PasarSaham();

        // Initialize View
        ConsoleView view = new ConsoleView();

        // Initialize Controller
        TradingController controller = new TradingController(view, auth, pasar);

        // Start the application
        controller.start();
    }
}
