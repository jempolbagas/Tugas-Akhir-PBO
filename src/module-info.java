module StockTradingApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports stocktradingapp;
    opens stocktradingapp to javafx.graphics, javafx.fxml;
}