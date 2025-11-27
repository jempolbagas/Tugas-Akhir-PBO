module StockTradingApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires org.junit.jupiter.api;
    requires org.mockito;

    exports StockTradingApp;
    opens StockTradingApp to javafx.graphics, javafx.fxml, com.google.gson, org.junit.platform.commons;
}