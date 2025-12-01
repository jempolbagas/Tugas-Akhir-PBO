package main.java.StockTradingApp.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Portfolio;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.service.MarketService;

import java.math.BigDecimal;

public class PortfolioView {
    private final Akun account;
    private final MarketService marketService;

    public PortfolioView(Akun account, MarketService marketService) {
        this.account = account;
        this.marketService = marketService;
    }

    public Node getView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (account == null || account.getPortfolio().isEmpty()) {
            Label emptyLabel = new Label("🚀 NO QUANTUM ASSETS DETECTED\nInitiate your first trade to begin portfolio construction.");
            emptyLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 40;");
            content.getChildren().add(emptyLabel);
            return new ScrollPane(content);
        }

        Label title = new Label("QUANTUM PORTFOLIO ANALYSIS");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        // Create a simple list view for portfolio
        ListView<String> portfolioList = new ListView<>();
        portfolioList.setStyle("-fx-background-color: transparent; -fx-border-color: #444477; -fx-border-radius: 8;");

        for (Portfolio port : account.getPortfolio().values()) {
            try {
                Saham saham = marketService.getSaham(port.getKodeSaham());
                BigDecimal nilaiSkrg = port.hitungNilaiSekarang(saham.getHargaSekarang());
                BigDecimal profit = port.hitungKeuntungan(saham.getHargaSekarang());
                BigDecimal persentase = port.hitungPersentaseKeuntungan(saham.getHargaSekarang());

                String portfolioInfo = String.format("%-8s %-20s %,10d Rp %,10.2f Rp %,10.2f %s%,10.2f (%.2f%%)",
                        port.getKodeSaham(),
                        port.getNamaSaham().length() > 20 ?
                                port.getNamaSaham().substring(0, 17) + "..." : port.getNamaSaham(),
                        port.getJumlah(),
                        port.getHargaBeli(),
                        saham.getHargaSekarang(),
                        profit.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "",
                        profit,
                        persentase);
                portfolioList.getItems().add(portfolioInfo);
            } catch (Exception e) {
                portfolioList.getItems().add("Error: " + e.getMessage());
            }
        }

        content.getChildren().addAll(title, portfolioList);
        return new ScrollPane(content);
    }
}
