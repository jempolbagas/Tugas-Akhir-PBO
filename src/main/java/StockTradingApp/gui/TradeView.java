package main.java.StockTradingApp.gui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.model.TradeResult;
import main.java.StockTradingApp.service.MarketService;
import main.java.StockTradingApp.service.TradingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TradeView {
    private final Akun account;
    private final MarketService marketService;
    private final TradingService tradingService;
    private final Runnable onTradeSuccess;
    
    // Track listeners and their associated TextFields for cleanup
    private final List<ListenerBinding> listenerBindings = new ArrayList<>();
    
    private static class ListenerBinding {
        final TextField textField;
        final ChangeListener<String> listener;
        
        ListenerBinding(TextField textField, ChangeListener<String> listener) {
            this.textField = textField;
            this.listener = listener;
        }
    }

    public TradeView(Akun account, MarketService marketService, TradingService tradingService, Runnable onTradeSuccess) {
        this.account = account;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.onTradeSuccess = onTradeSuccess;
    }

    public Node getView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("QUANTUM TRADE INTERFACE");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        HBox tradeContainer = new HBox(30);
        tradeContainer.setAlignment(Pos.CENTER);

        // Buy Section
        VBox buySection = createTradeSection("BUY", "🟢");
        VBox sellSection = createTradeSection("SELL", "🔴");

        tradeContainer.getChildren().addAll(buySection, sellSection);
        content.getChildren().addAll(title, tradeContainer);

        return content;
    }

    private VBox createTradeSection(String type, String emoji) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: rgba(26, 26, 46, 0.6); -fx-border-color: #444477; -fx-border-radius: 12; -fx-background-radius: 12;");
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(400);

        Label title = new Label(emoji + " QUANTUM " + type);
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #00ccff;");

        ComboBox<String> stockSelector = new ComboBox<>();
        for (Saham saham : marketService.getAllSaham()) {
            stockSelector.getItems().add(saham.getKode());
        }
        stockSelector.setPromptText("Select Quantum Asset");
        stockSelector.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white;");

        TextField quantityField = GUIUtils.createStyledTextField();
        quantityField.setPromptText("Quantity (lots)");

        Label priceLabel = new Label("Quantum Price: --");
        priceLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px;");

        Label totalLabel = new Label("Total Energy: --");
        totalLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px;");

        Button executeBtn = GUIUtils.createMenuButton("⚡ EXECUTE " + type + " ORDER",
                type.equals("BUY") ? "success" : "danger");

        // Update price when stock is selected
        stockSelector.setOnAction(e -> {
            String selected = stockSelector.getValue();
            if (selected != null) {
                try {
                    Saham saham = marketService.getSaham(selected);
                    priceLabel.setText("Quantum Price: Rp " + String.format("%,.2f", saham.getHargaSekarang()));
                    updateTotalLabel(quantityField, saham, totalLabel);
                } catch (Exception ex) {
                    priceLabel.setText("Quantum Price: --");
                }
            }
        });

        // Update total when quantity changes
        ChangeListener<String> quantityListener = (observable, oldValue, newValue) -> {
            String selected = stockSelector.getValue();
            if (selected != null && !newValue.isEmpty()) {
                try {
                    Saham saham = marketService.getSaham(selected);
                    updateTotalLabel(quantityField, saham, totalLabel);
                } catch (Exception ex) {
                    totalLabel.setText("Total Energy: --");
                }
            } else {
                totalLabel.setText("Total Energy: --");
            }
        };
        quantityField.textProperty().addListener(quantityListener);
        listenerBindings.add(new ListenerBinding(quantityField, quantityListener));

        executeBtn.setOnAction(e -> {
            if (account == null) {
                GUIUtils.showAlert("Quantum Error", "Please login to trade", Alert.AlertType.ERROR);
                return;
            }

            String selectedStock = stockSelector.getValue();
            String quantityText = quantityField.getText();

            if (selectedStock == null || quantityText.isEmpty()) {
                GUIUtils.showAlert("Quantum Error", "Please select stock and enter quantity", Alert.AlertType.ERROR);
                return;
            }

            try {
                int lot = Integer.parseInt(quantityText);
                int jumlahLembar = lot * 100;

                TradeResult result;
                if (type.equals("BUY")) {
                    result = tradingService.buyStock(account, selectedStock, jumlahLembar);
                } else {
                    result = tradingService.sellStock(account, selectedStock, jumlahLembar);
                }

                if (result.isSuccess()) {
                    GUIUtils.showAlert("Quantum Success", result.getMessage(), Alert.AlertType.INFORMATION);

                    // Clear form
                    stockSelector.setValue(null);
                    quantityField.clear();
                    priceLabel.setText("Quantum Price: --");
                    totalLabel.setText("Total Energy: --");

                    if (onTradeSuccess != null) {
                        onTradeSuccess.run();
                    }

                } else {
                    GUIUtils.showAlert("Quantum Error", result.getMessage(), Alert.AlertType.ERROR);
                }

            } catch (NumberFormatException ex) {
                GUIUtils.showAlert("Quantum Error", "Invalid quantity format", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                GUIUtils.showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        section.getChildren().addAll(title, stockSelector, quantityField, priceLabel, totalLabel, executeBtn);
        return section;
    }

    private void updateTotalLabel(TextField quantityField, Saham saham, Label totalLabel) {
        try {
            int lot = Integer.parseInt(quantityField.getText());
            int jumlahLembar = lot * 100;
            BigDecimal total = saham.getHargaSekarang().multiply(BigDecimal.valueOf(jumlahLembar));
            totalLabel.setText("Total Energy: Rp " + String.format("%,.2f", total));
        } catch (NumberFormatException e) {
            totalLabel.setText("Total Energy: --");
        }
    }

    public void dispose() {
        // Remove all registered listeners
        for (ListenerBinding binding : listenerBindings) {
            binding.textField.textProperty().removeListener(binding.listener);
        }
        listenerBindings.clear();
    }
}
