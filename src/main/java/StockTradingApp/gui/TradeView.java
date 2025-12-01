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
import main.java.StockTradingApp.model.Portfolio;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.model.TradeResult;
import main.java.StockTradingApp.service.MarketService;
import main.java.StockTradingApp.service.TradingService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * View class responsible for handling stock trades (Buy/Sell).
 * Provides a user interface for selecting stocks, specifying quantity, and executing orders.
 */
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

    /**
     * Constructs a new TradeView.
     *
     * @param account        The user account.
     * @param marketService  The market service.
     * @param tradingService The trading service.
     * @param onTradeSuccess A callback to execute after a successful trade.
     */
    public TradeView(Akun account, MarketService marketService, TradingService tradingService, Runnable onTradeSuccess) {
        this.account = account;
        this.marketService = marketService;
        this.tradingService = tradingService;
        this.onTradeSuccess = onTradeSuccess;
    }

    /**
     * Builds and returns the trade UI view.
     * Contains separate sections for Buying and Selling.
     *
     * @return A Node containing the trade interface.
     */
    public Node getView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("QUANTUM TRADE INTERFACE");
        title.getStyleClass().add("trade-view-title");

        HBox tradeContainer = new HBox(30);
        tradeContainer.setAlignment(Pos.CENTER);

        // Buy Section
        VBox buySection = createTradeSection("BUY", "🟢");
        VBox sellSection = createTradeSection("SELL", "🔴");

        tradeContainer.getChildren().addAll(buySection, sellSection);
        content.getChildren().addAll(title, tradeContainer);

        return content;
    }

    /**
     * Creates a trade section (Buy or Sell) with all necessary controls.
     *
     * @param type  The type of trade ("BUY" or "SELL").
     * @param emoji An emoji icon for the title.
     * @return A VBox containing the trade form.
     */
    private VBox createTradeSection(String type, String emoji) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.getStyleClass().add("trade-card");
        section.setAlignment(Pos.CENTER);
        section.setMaxWidth(400);

        Label title = new Label(emoji + " QUANTUM " + type);
        title.getStyleClass().add("trade-card-title");

        ComboBox<String> stockSelector = new ComboBox<>();
        for (Saham saham : marketService.getAllSaham()) {
            stockSelector.getItems().add(saham.getKode());
        }
        stockSelector.setPromptText("Select Quantum Asset");
        stockSelector.getStyleClass().add("trade-stock-selector");

        TextField quantityField = GUIUtils.createStyledTextField();
        quantityField.setPromptText("Quantity (lots)");

        // Quick Quantity Buttons
        HBox quickQtyBox = new HBox(10);
        quickQtyBox.setAlignment(Pos.CENTER);
        Button btn25 = createQuickQtyButton("25%");
        Button btn50 = createQuickQtyButton("50%");
        Button btnMax = createQuickQtyButton("MAX");
        quickQtyBox.getChildren().addAll(btn25, btn50, btnMax);

        Label priceLabel = new Label("Quantum Price: --");
        priceLabel.getStyleClass().add("price-label");

        Label totalLabel = new Label("Total Energy: --");
        totalLabel.getStyleClass().add("total-label");

        Button executeBtn = GUIUtils.createMenuButton("⚡ EXECUTE " + type + " ORDER",
                type.equals("BUY") ? "success" : "danger");

        btn25.setOnAction(e -> applyQuickQuantity(0.25, type, stockSelector.getValue(), quantityField));
        btn50.setOnAction(e -> applyQuickQuantity(0.50, type, stockSelector.getValue(), quantityField));
        btnMax.setOnAction(e -> applyQuickQuantity(1.00, type, stockSelector.getValue(), quantityField));

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

        section.getChildren().addAll(title, stockSelector, quantityField, quickQtyBox, priceLabel, totalLabel, executeBtn);
        return section;
    }

    /**
     * Creates a quick quantity selection button (e.g., 25%, 50%, MAX).
     *
     * @param text The button text.
     * @return A styled Button.
     */
    private Button createQuickQtyButton(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("quick-qty-button");
        return btn;
    }

    /**
     * Calculates and sets the quantity field based on the selected percentage.
     * Logic differs for Buy (based on balance) and Sell (based on ownership).
     *
     * @param percentage    The percentage (0.25, 0.50, 1.00).
     * @param type          The trade type.
     * @param stockCode     The selected stock.
     * @param quantityField The text field to update.
     */
    private void applyQuickQuantity(double percentage, String type, String stockCode, TextField quantityField) {
        if (stockCode == null || account == null) return;

        try {
            int maxLots = 0;
            if (type.equals("BUY")) {
                Saham saham = marketService.getSaham(stockCode);
                BigDecimal price = saham.getHargaSekarang();
                BigDecimal balance = account.getSaldo();

                // Logic: floor((Balance / Price) / 100)
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal maxShares = balance.divide(price, 2, java.math.RoundingMode.FLOOR);
                    maxLots = maxShares.divide(BigDecimal.valueOf(100), 0, java.math.RoundingMode.FLOOR).intValue();
                }
            } else {
                // SELL
                if (account.getPortfolio().containsKey(stockCode)) {
                    Portfolio portfolio = account.getPortfolio().get(stockCode);
                    int ownedShares = portfolio.getJumlah();
                    maxLots = ownedShares / 100;
                }
            }

            int targetLots = (int) (maxLots * percentage);
            quantityField.setText(String.valueOf(targetLots));

        } catch (Exception e) {
            // Silently fail or log if calculation fails (e.g. stock not found)
            System.err.println("Error calculating quick quantity: " + e.getMessage());
        }
    }

    /**
     * Updates the total price label based on quantity and current stock price.
     *
     * @param quantityField The quantity input field.
     * @param saham         The selected stock.
     * @param totalLabel    The label to update.
     */
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

    /**
     * Cleans up resources by removing listeners attached to UI controls.
     */
    public void dispose() {
        // Remove all registered listeners
        for (ListenerBinding binding : listenerBindings) {
            binding.textField.textProperty().removeListener(binding.listener);
        }
        listenerBindings.clear();
    }
}
