package main.java.StockTradingApp.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Portfolio;
import main.java.StockTradingApp.model.Saham;
import main.java.StockTradingApp.service.MarketService;

import java.math.BigDecimal;

public class PortfolioView {
    private final Akun account;
    private final MarketService marketService;
    private Runnable marketListener;
    private TableView<Portfolio> table;

    public PortfolioView(Akun account, MarketService marketService) {
        this.account = account;
        this.marketService = marketService;
    }

    private BigDecimal getHargaSekarangSafe(String kode) {
        try {
            Saham s = marketService.getSaham(kode);
            return s.getHargaSekarang();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public Node getView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: transparent;");

        // Empty State
        if (account == null || account.getPortfolio().isEmpty()) {
            Label emptyLabel = new Label("🚀 NO QUANTUM ASSETS DETECTED\nInitiate your first trade to begin portfolio construction.");
            emptyLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 40;");

            VBox emptyContainer = new VBox(emptyLabel);
            emptyContainer.setAlignment(Pos.CENTER);
            VBox.setVgrow(emptyContainer, Priority.ALWAYS);
            content.getChildren().add(emptyContainer);
            return content;
        }

        Label title = new Label("QUANTUM PORTFOLIO ANALYSIS");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        // Table Setup
        table = new TableView<>();
        table.getStyleClass().add("futuristic-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        // 1. Symbol Column
        TableColumn<Portfolio, String> colSymbol = new TableColumn<>("Symbol");
        colSymbol.setCellValueFactory(new PropertyValueFactory<>("kodeSaham"));
        colSymbol.setCellFactory(col -> new TableCell<Portfolio, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        // 2. Quantity Column
        TableColumn<Portfolio, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colQty.setCellFactory(col -> new TableCell<Portfolio, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%,d", item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // 3. Avg Buy Price
        TableColumn<Portfolio, BigDecimal> colAvgPrice = new TableColumn<>("Avg Buy Price");
        colAvgPrice.setCellValueFactory(new PropertyValueFactory<>("hargaBeli"));
        colAvgPrice.setCellFactory(col -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // 4. Current Price (Dynamic)
        TableColumn<Portfolio, BigDecimal> colCurPrice = new TableColumn<>("Current Price");
        colCurPrice.setCellValueFactory(data -> {
            Portfolio p = data.getValue();
            return new SimpleObjectProperty<>(getHargaSekarangSafe(p.getKodeSaham()));
        });
        colCurPrice.setCellFactory(col -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // 5. Total Value (Dynamic)
        TableColumn<Portfolio, BigDecimal> colTotalValue = new TableColumn<>("Total Value");
        colTotalValue.setCellValueFactory(data -> {
            Portfolio p = data.getValue();
            BigDecimal price = getHargaSekarangSafe(p.getKodeSaham());
            return new SimpleObjectProperty<>(p.hitungNilaiSekarang(price));
        });
        colTotalValue.setCellFactory(col -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));
                    setStyle("-fx-text-fill: #00ccff;"); // Cyan for value
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // 6. Unrealized P/L (Rp)
        TableColumn<Portfolio, BigDecimal> colPLValue = new TableColumn<>("Unrealized P/L (Rp)");
        colPLValue.setCellValueFactory(data -> {
            Portfolio p = data.getValue();
            BigDecimal currentPrice = getHargaSekarangSafe(p.getKodeSaham());
            return new SimpleObjectProperty<>(p.hitungKeuntungan(currentPrice));
        });
        colPLValue.setCellFactory(col -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("text-positive", "text-negative");
                    setStyle("");
                } else {
                    setText(String.format("%sRp %,.2f", item.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", item));

                    getStyleClass().removeAll("text-positive", "text-negative");

                    if (item.compareTo(BigDecimal.ZERO) > 0) {
                        getStyleClass().add("text-positive");
                        setStyle(""); // Clear potential inline style
                    } else if (item.compareTo(BigDecimal.ZERO) < 0) {
                        getStyleClass().add("text-negative");
                        setStyle(""); // Clear potential inline style
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        // 7. Unrealized P/L (%)
        TableColumn<Portfolio, BigDecimal> colPLPercent = new TableColumn<>("Unrealized P/L (%)");
        colPLPercent.setCellValueFactory(data -> {
            Portfolio p = data.getValue();
            BigDecimal currentPrice = getHargaSekarangSafe(p.getKodeSaham());
            try {
                return new SimpleObjectProperty<>(p.hitungPersentaseKeuntungan(currentPrice));
            } catch (IllegalStateException e) {
                // Return zero if total modal is zero to prevent crash
                return new SimpleObjectProperty<>(BigDecimal.ZERO);
            }
        });
        colPLPercent.setCellFactory(col -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    getStyleClass().removeAll("text-positive", "text-negative");
                    setStyle("");
                } else {
                    setText(String.format("%s%.2f%%", item.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", item));

                    getStyleClass().removeAll("text-positive", "text-negative");

                    if (item.compareTo(BigDecimal.ZERO) > 0) {
                        getStyleClass().add("text-positive");
                        setStyle("");
                    } else if (item.compareTo(BigDecimal.ZERO) < 0) {
                        getStyleClass().add("text-negative");
                        setStyle("");
                    } else {
                        setStyle("-fx-text-fill: white;");
                    }
                    setAlignment(Pos.CENTER_RIGHT);
                }
            }
        });

        table.getColumns().addAll(colSymbol, colQty, colAvgPrice, colCurPrice, colTotalValue, colPLValue, colPLPercent);

        // Populate Data
        if (account != null && account.getPortfolio() != null) {
            table.getItems().addAll(account.getPortfolio().values());
        }

        // Register Real-time Listener
        marketListener = () -> {
            Platform.runLater(() -> {
                table.refresh(); // Triggers CellValueFactory re-evaluation for calculated columns
            });
        };
        marketService.addListener(marketListener);

        content.getChildren().addAll(title, table);
        return content;
    }

    public void dispose() {
        if (marketListener != null) {
            marketService.removeListener(marketListener);
            marketListener = null;
        }
    }
}
