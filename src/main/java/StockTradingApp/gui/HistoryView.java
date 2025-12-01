package main.java.StockTradingApp.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Transaksi;

import java.util.ArrayList;

public class HistoryView {
    private final Akun account;

    public HistoryView(Akun account) {
        this.account = account;
    }

    public Node getView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("QUANTUM TRANSACTION HISTORY");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        if (account == null || account.getRiwayatTransaksi().isEmpty()) {
            Label emptyLabel = new Label("📊 NO TRANSACTION HISTORY\nYour transactions will appear here.");
            emptyLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 40;");
            content.getChildren().addAll(title, emptyLabel);
            return new ScrollPane(content);
        }

        // Create a simple list view for history
        ListView<String> historyList = new ListView<>();
        historyList.setStyle("-fx-background-color: transparent; -fx-border-color: #444477; -fx-border-radius: 8;");

        ArrayList<Transaksi> riwayat = account.getRiwayatTransaksi();
        for (int i = riwayat.size() - 1; i >= 0 && i >= riwayat.size() - 20; i--) {
            historyList.getItems().add(riwayat.get(i).toString());
        }

        content.getChildren().addAll(title, historyList);
        return new ScrollPane(content);
    }
}
