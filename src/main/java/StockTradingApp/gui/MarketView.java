package main.java.StockTradingApp.gui;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.service.MarketService;

import java.math.BigDecimal;
import java.util.List;

public class MarketView {
    private final MarketService marketService;
    private Runnable marketListener;
    private boolean viewCreated = false;
    private Node cachedView;

    public MarketView(MarketService marketService) {
        this.marketService = marketService;
    }

    public Node getView() {
        // Guard to prevent multiple listener registrations if getView() is called multiple times
        if (viewCreated) {
            return cachedView;
        }
        viewCreated = true;
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.3);
        splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 20;");

        // Left: Stock List (Master)
        VBox listContainer = new VBox(10);
        Label listTitle = new Label("MARKET LIST");
        listTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #00ccff;");

        ListView<Saham> stockList = new ListView<>();
        stockList.setStyle("-fx-background-color: transparent; -fx-border-color: #444477; -fx-border-radius: 8;");
        stockList.setCellFactory(param -> new ListCell<Saham>() {
            private final Label lblCode = new Label();
            private final Label lblName = new Label();
            private final Label lblPrice = new Label();
            private final Label lblChange = new Label();
            private final VBox leftBox = new VBox(5, lblCode, lblName);
            private final VBox rightBox = new VBox(5, lblPrice, lblChange);
            private final HBox root = new HBox(10, leftBox, rightBox);

            {
                // Layout Configuration
                root.setAlignment(Pos.CENTER_LEFT);
                HBox.setHgrow(leftBox, Priority.ALWAYS);
                rightBox.setAlignment(Pos.CENTER_RIGHT);

                // Style Configuration
                lblCode.setStyle("-fx-text-fill: #00ccff; -fx-font-weight: bold; -fx-font-size: 14px;");
                lblName.setStyle("-fx-text-fill: #8888ff; -fx-font-size: 10px;");
                lblPrice.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");
                lblChange.setStyle("-fx-font-size: 10px;");

                getStyleClass().add("market-list-cell");
            }

            @Override
            protected void updateItem(Saham item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    lblCode.setText(item.getKode());
                    lblName.setText(item.getNamaSaham());
                    lblPrice.setText(String.format("Rp %,.2f", item.getHargaSekarang()));

                    String changeText = item.getPerubahanFormatted();
                    lblChange.setText(changeText);

                    // Reset classes to avoid accumulation
                    lblChange.getStyleClass().removeAll("text-positive", "text-negative");
                    if (item.getPerubahan().compareTo(BigDecimal.ZERO) >= 0) {
                        lblChange.getStyleClass().add("text-positive");
                    } else {
                        lblChange.getStyleClass().add("text-negative");
                    }

                    setGraphic(root);
                    setText(null);
                }
            }
        });
        stockList.getItems().addAll(marketService.getAllSaham());
        VBox.setVgrow(stockList, Priority.ALWAYS);

        listContainer.getChildren().addAll(listTitle, stockList);

        // Right: Chart (Detail)
        VBox chartContainer = new VBox(10);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        xAxis.setStyle("-fx-tick-label-fill: #8888ff; -fx-text-fill: #8888ff;");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price (Rp)");
        yAxis.setStyle("-fx-tick-label-fill: #8888ff; -fx-text-fill: #8888ff;");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Market Trend");
        lineChart.getStyleClass().add("crypto-chart");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        VBox.setVgrow(lineChart, Priority.ALWAYS);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        lineChart.getData().add(series);

        chartContainer.getChildren().add(lineChart);

        // Logic: Selection Listener
        stockList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateChartData(lineChart, series, newVal);
            }
        });

        // Logic: Market Update Listener
        // Remove any previously registered listener to prevent accumulation
        // just in case dispose wasn't called (though it should be)
        if (marketListener != null) {
            marketService.removeListener(marketListener);
        }

        marketListener = () -> {
            Platform.runLater(() -> {
                stockList.refresh();
                Saham selected = stockList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    updateChartData(lineChart, series, selected);
                }
            });
        };
        marketService.addListener(marketListener);

        // Initial Selection
        if (!stockList.getItems().isEmpty()) {
            stockList.getSelectionModel().select(0);
        }

        splitPane.getItems().addAll(listContainer, chartContainer);
        cachedView = splitPane;
        return splitPane;
    }

    private void updateChartData(LineChart<String, Number> chart, XYChart.Series<String, Number> series, Saham saham) {
        chart.setTitle(saham.getNamaSaham() + " (" + saham.getKode() + ")");
        series.getData().clear();

        List<BigDecimal> prices = saham.getPriceHistory();
        List<String> times = saham.getTimeHistory();

        for (int i = 0; i < prices.size(); i++) {
            series.getData().add(new XYChart.Data<>(times.get(i), prices.get(i)));
        }
    }

    public void dispose() {
        if (marketListener != null) {
            marketService.removeListener(marketListener);
            marketListener = null;
        }
    }
}
