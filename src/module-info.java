module main.java.StockTradingApp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires org.junit.jupiter.api;
    requires org.mockito;

    exports main.java.StockTradingApp;
    opens main.java.StockTradingApp to javafx.graphics, javafx.fxml, com.google.gson, org.junit.platform.commons;

    exports main.java.StockTradingApp.model;
    opens main.java.StockTradingApp.model to com.google.gson, javafx.fxml, javafx.graphics, org.junit.platform.commons;

    exports main.java.StockTradingApp.service;
    opens main.java.StockTradingApp.service to com.google.gson, javafx.fxml, javafx.graphics, org.junit.platform.commons;

    exports main.java.StockTradingApp.gui;
    opens main.java.StockTradingApp.gui to com.google.gson, javafx.fxml, javafx.graphics, org.junit.platform.commons;

    exports main.java.StockTradingApp.cli;
    opens main.java.StockTradingApp.cli to com.google.gson, javafx.fxml, javafx.graphics, org.junit.platform.commons;

    exports test.java.StockTradingApp.service;
    opens test.java.StockTradingApp.service to com.google.gson, javafx.fxml, javafx.graphics, org.junit.platform.commons;
}