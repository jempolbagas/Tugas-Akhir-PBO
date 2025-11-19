module StockTradingApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;

    exports StockTradingApp;
    opens StockTradingApp to javafx.graphics, javafx.fxml, com.google.gson;
}