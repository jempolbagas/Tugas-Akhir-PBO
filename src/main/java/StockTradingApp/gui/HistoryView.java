package main.java.StockTradingApp.gui;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.java.StockTradingApp.model.Akun;
import main.java.StockTradingApp.model.Transaksi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryView {
    private final Akun account;
    private FilteredList<Transaksi> filteredData;
    private VBox detailPanel;
    private Label detailHeader;
    private Label detailSummary;
    private Label detailImpact;

    public HistoryView(Akun account) {
        this.account = account;
    }

    public Node getView() {
        // Main Container is now a SplitPane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        // === Top Section: Table & Controls ===
        VBox topSection = new VBox(15);
        topSection.setPadding(new Insets(20));
        topSection.setFillWidth(true);

        Label title = new Label("QUANTUM TRANSACTION HISTORY");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        // Controls
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(0, 0, 10, 0));
        controls.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = GUIUtils.createStyledTextField();
        searchField.setPromptText("Search Ticker or ID...");
        searchField.setPrefWidth(250);

        ComboBox<String> typeFilter = new ComboBox<>();
        typeFilter.setItems(FXCollections.observableArrayList("ALL", "BUY", "SELL", "TOPUP"));
        typeFilter.setValue("ALL");
        typeFilter.getStyleClass().add("futuristic-combo");
        typeFilter.setPrefWidth(120);

        controls.getChildren().addAll(searchField, typeFilter);

        // Data Setup
        ObservableList<Transaksi> masterData = FXCollections.observableArrayList();
        if (account != null && !account.getRiwayatTransaksi().isEmpty()) {
            masterData.addAll(account.getRiwayatTransaksi());
        }

        filteredData = new FilteredList<>(masterData, p -> true);
        SortedList<Transaksi> sortedData = new SortedList<>(filteredData);

        // TableView
        TableView<Transaksi> table = new TableView<>();
        table.setItems(sortedData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        table.getStyleClass().add("futuristic-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define Columns
        // ID
        TableColumn<Transaksi, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        idCol.setPrefWidth(120);
        idCol.setMaxWidth(150);

        // Date
        TableColumn<Transaksi, LocalDateTime> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        dateCol.setCellFactory(column -> new TableCell<Transaksi, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
        dateCol.setPrefWidth(150);

        // Type
        TableColumn<Transaksi, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        typeCol.setCellFactory(column -> new TableCell<Transaksi, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("table-cell-positive", "table-cell-negative");

                if (empty || item == null) {
                    setText(null);
                } else {
                    if ("BUY".equalsIgnoreCase(item) || "TOPUP".equalsIgnoreCase(item)) {
                        setText("🟢 " + item);
                        getStyleClass().add("table-cell-positive");
                    } else if ("SELL".equalsIgnoreCase(item)) {
                        setText("🔴 " + item);
                        getStyleClass().add("table-cell-negative");
                    } else {
                        setText(item);
                    }
                }
            }
        });
        typeCol.setPrefWidth(60);
        typeCol.setMaxWidth(100);

        // Symbol
        TableColumn<Transaksi, String> symbolCol = new TableColumn<>("Symbol");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("kodeSaham"));
        symbolCol.setPrefWidth(80);
        symbolCol.setMaxWidth(100);

        // Volume
        TableColumn<Transaksi, Integer> volCol = new TableColumn<>("Volume");
        volCol.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        volCol.setPrefWidth(80);
        volCol.setMaxWidth(120);

        // Price
        TableColumn<Transaksi, BigDecimal> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("harga"));
        priceCol.setCellFactory(column -> new TableCell<Transaksi, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                setAlignment(Pos.CENTER_RIGHT);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));
                }
            }
        });
        priceCol.setPrefWidth(120);

        // Total
        TableColumn<Transaksi, BigDecimal> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setCellFactory(column -> new TableCell<Transaksi, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("table-cell-positive", "table-cell-negative");
                setAlignment(Pos.CENTER_RIGHT);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("Rp %,.2f", item));

                    // Access the row data to check type safely
                    Transaksi trx = getTableRow().getItem();
                    if (trx != null) {
                        String type = trx.getJenis();

                        if ("SELL".equalsIgnoreCase(type) || "TOPUP".equalsIgnoreCase(type)) {
                            getStyleClass().add("table-cell-positive"); // Cash In
                        } else if ("BUY".equalsIgnoreCase(type)) {
                            getStyleClass().add("table-cell-negative"); // Cash Out
                        }
                    }
                }
            }
        });
        totalCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, dateCol, typeCol, symbolCol, volCol, priceCol, totalCol);

        // Sort by Date Descending
        dateCol.setSortType(TableColumn.SortType.DESCENDING);
        table.getSortOrder().add(dateCol);

        // Filter Listeners
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
            updateFilter(newValue, typeFilter.getValue()));
        typeFilter.valueProperty().addListener((observable, oldValue, newValue) ->
            updateFilter(searchField.getText(), newValue));

        topSection.getChildren().addAll(title, controls, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // === Bottom Section: Detail Panel ===
        detailPanel = new VBox(15);
        detailPanel.setPadding(new Insets(20));
        detailPanel.getStyleClass().add("glass-card");
        detailPanel.setFillWidth(true);
        detailPanel.setAlignment(Pos.CENTER_LEFT);
        detailPanel.setVisible(false);
        detailPanel.setOpacity(0);

        detailHeader = new Label("TRANSACTION DETAILS");
        detailHeader.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #8888ff;");

        detailSummary = new Label("Select a transaction");
        detailSummary.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        detailImpact = new Label("Impact on Balance: -");
        detailImpact.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-text-fill: #00ccff;");

        Button deselectBtn = GUIUtils.createMenuButton("Deselect", "secondary");
        deselectBtn.setMaxWidth(150);
        deselectBtn.setOnAction(e -> table.getSelectionModel().clearSelection());

        detailPanel.getChildren().addAll(detailHeader, detailSummary, detailImpact, deselectBtn);

        // Selection Logic
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                showDetails(newSelection);
            } else {
                hideDetails();
            }
        });

        // Add to SplitPane
        splitPane.getItems().addAll(topSection, detailPanel);
        splitPane.setDividerPositions(0.75);

        return splitPane;
    }

    private void updateFilter(String searchText, String type) {
        filteredData.setPredicate(trx -> {
            if (type != null && !"ALL".equals(type) && !type.equalsIgnoreCase(trx.getJenis())) {
                return false;
            }
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            String id = trx.getIdTransaksi();
            String kode = trx.getKodeSaham();
            return (id != null && id.toLowerCase().contains(lowerCaseFilter)) ||
                   (kode != null && kode.toLowerCase().contains(lowerCaseFilter));
        });
    }

    private void showDetails(Transaksi trx) {
        String type = trx.getJenis();
        String symbol = trx.getKodeSaham();
        int vol = trx.getJumlah();
        BigDecimal total = trx.getTotal();

        // 1. Set Header
        detailHeader.setText("TRANSACTION DETAILS #" + trx.getIdTransaksi());

        // 2. Set Summary
        if ("TOPUP".equalsIgnoreCase(type)) {
             detailSummary.setText("TOP UP ACCOUNT");
        } else {
             detailSummary.setText(String.format("%s %d LOTS OF %s", type, vol, symbol));
        }

        // 3. Set Impact
        if ("BUY".equalsIgnoreCase(type)) {
            detailImpact.setText(String.format("Impact on Balance: - Rp %,.2f", total));
            detailImpact.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-text-fill: #ff4444;"); // Red
        } else {
            // SELL or TOPUP -> Positive
            detailImpact.setText(String.format("Impact on Balance: + Rp %,.2f", total));
            detailImpact.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-text-fill: #00ff88;"); // Green
        }

        // 4. Animation
        if (!detailPanel.isVisible()) {
            detailPanel.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(300), detailPanel);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }
    }

    private void hideDetails() {
        if (detailPanel.isVisible()) {
            FadeTransition ft = new FadeTransition(Duration.millis(300), detailPanel);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> detailPanel.setVisible(false));
            ft.play();
        }
    }

    public void dispose() {
        // No listeners to clean up
    }
}
