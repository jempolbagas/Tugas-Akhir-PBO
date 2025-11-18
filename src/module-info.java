module StockTradingApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports StockTradingApp;
    opens StockTradingApp to javafx.graphics, javafx.fxml;
}