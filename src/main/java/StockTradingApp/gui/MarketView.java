package main.java.StockTradingApp.gui;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.service.MarketService;

import java.math.BigDecimal;
import java.util.List;

public class MarketView {
    private final MarketService marketService;
    private Runnable marketListener;

    public MarketView(MarketService marketService) {
        this.marketService = marketService;
    }

    public Node getView() {
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
            @Override
            protected void updateItem(Saham item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
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
