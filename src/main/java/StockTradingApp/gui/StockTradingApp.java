package main.java.StockTradingApp.gui;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.StockTradingApp.model.*;
import main.java.StockTradingApp.service.MarketService;
import main.java.StockTradingApp.service.SistemAutentikasi;
import main.java.StockTradingApp.service.TradingService;

public class StockTradingApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private SistemAutentikasi auth;
    private MarketService marketService;
    private TradingService tradingService;
    private Akun akunAktif = null;
    private Label balanceLabel;

    private TableView<Saham> marketTable;
    private TableView<Portfolio> portfolioTable;
    private static final NumberFormat IDR_FORMAT = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("NeoStock - Futuristic Trading Platform");

        try {
            auth = new SistemAutentikasi();
            marketService = new MarketService();
            tradingService = new TradingService(marketService, auth);

            // Show notifications if any
            List<String> notifications = auth.getNotifications();
            if (!notifications.isEmpty()) {
                showAlert("System Notification", String.join("\n", notifications), Alert.AlertType.INFORMATION);
            }

            // Register reactive listener for market updates
            marketService.addListener(() -> {
                Platform.runLater(() -> {
                    if (marketTable != null) marketTable.refresh();
                    if (portfolioTable != null) portfolioTable.refresh();
                });
            });

        } catch (Exception e) {
            showAlert("Critical Error", "Failed to load or save data: " + e.getMessage(), Alert.AlertType.ERROR);
            // Optionally, we can decide to exit or disable features. For now, we just show an error.
        }

        initRootLayout();
        showSplashScreen();

        // Start background price updates
        if (marketService != null) {
            marketService.startMarketUpdates();
        }
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        rootLayout.getStyleClass().add("root-layout");

        Scene scene = new Scene(rootLayout, 1200, 800);
        // Load CSS from same directory
        try {
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS file not found, using default styling");
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showSplashScreen() {
        VBox splashContainer = new VBox(30);
        splashContainer.setAlignment(Pos.CENTER);
        splashContainer.getStyleClass().add("splash-container");

        // Animated title
        Label title = new Label("NEO-STOCK");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        Label subtitle = new Label("QUANTUM TRADING PLATFORM");
        subtitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 16px; -fx-text-fill: #8888ff;");

        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: linear-gradient(to right, #00ff88, #00ccff);");

        Label loadingText = new Label("Initializing Quantum Engine...");
        loadingText.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 12px;");

        splashContainer.getChildren().addAll(title, subtitle, progress, loadingText);
        rootLayout.setCenter(splashContainer);

        // Animate splash screen
        FadeTransition fade = new FadeTransition(Duration.seconds(2), splashContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        fade.setOnFinished(e -> {
            showMainMenu();
        });
    }

    private void showMainMenu() {
        VBox mainMenu = new VBox(30);
        mainMenu.setAlignment(Pos.CENTER);
        mainMenu.setPadding(new Insets(50));
        mainMenu.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a12, #151528);");

        Label title = new Label("NEO-STOCK");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 42px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setMaxWidth(300);

        Button btnRegister = createMenuButton("🆕 QUANTUM REGISTER", "primary");
        Button btnLogin = createMenuButton("🔐 QUANTUM LOGIN", "secondary");
        Button btnGuest = createMenuButton("👁️ GUEST MODE", "tertiary");
        Button btnExit = createMenuButton("⏻ EXIT SYSTEM", "danger");

        btnRegister.setOnAction(e -> showRegistrationForm());
        btnLogin.setOnAction(e -> showLoginForm());
        btnGuest.setOnAction(e -> showGuestDashboard());
        btnExit.setOnAction(e -> primaryStage.close());

        buttonContainer.getChildren().addAll(btnRegister, btnLogin, btnGuest, btnExit);

        // Market status
        HBox marketStatus = new HBox(10);
        marketStatus.setAlignment(Pos.CENTER);
        Label marketLabel = new Label("Market Status:");
        marketLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px;");

        // Safety check for marketService in case it failed init (though try/catch above handles it mostly)
        boolean isOpen = (marketService != null) && marketService.isPasarBuka();

        Label marketValue = new Label(isOpen ? "🟢 QUANTUM ACTIVE" : "🔴 QUANTUM OFFLINE");
        marketValue.setStyle(isOpen ?
                "-fx-font-family: 'Segoe UI'; -fx-text-fill: #00ff88; -fx-font-weight: bold; -fx-font-size: 12px;" :
                "-fx-font-family: 'Segoe UI'; -fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-font-size: 12px;");
        marketStatus.getChildren().addAll(marketLabel, marketValue);

        mainMenu.getChildren().addAll(title, buttonContainer, marketStatus);
        rootLayout.setCenter(mainMenu);
    }

    private Button createMenuButton(String text, String styleClass) {
        Button button = new Button(text);
        button.setStyle(getButtonStyle(styleClass));
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(40);
        return button;
    }

    private String getButtonStyle(String styleClass) {
        switch (styleClass) {
            case "primary":
                return "-fx-background-color: linear-gradient(to right, #00ff88, #00ccff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "secondary":
                return "-fx-background-color: linear-gradient(to right, #6666ff, #8888ff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "tertiary":
                return "-fx-background-color: transparent; -fx-border-color: #444477; -fx-text-fill: #8888ff; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "danger":
                return "-fx-background-color: linear-gradient(to right, #ff4444, #ff6666); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            case "success":
                return "-fx-background-color: linear-gradient(to right, #00ff88, #00ccff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
            default:
                return "-fx-background-color: #444477; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;";
        }
    }

    private void showRegistrationForm() {
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(40));
        formContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a12, #151528);");

        Label title = new Label("QUANTUM REGISTRATION");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(500);

        TextField tfUsername = createStyledTextField();
        PasswordField pfPassword = createStyledPasswordField();
        TextField tfFullName = createStyledTextField();
        TextField tfEmail = createStyledTextField();
        TextField tfDeposit = createStyledTextField();

        form.add(createFormLabel("Quantum ID:"), 0, 0);
        form.add(tfUsername, 1, 0);
        form.add(createFormLabel("Security Code:"), 0, 1);
        form.add(pfPassword, 1, 1);
        form.add(createFormLabel("Operator Name:"), 0, 2);
        form.add(tfFullName, 1, 2);
        form.add(createFormLabel("Quantum Mail:"), 0, 3);
        form.add(tfEmail, 1, 3);
        form.add(createFormLabel("Initial Fuel:"), 0, 4);
        form.add(tfDeposit, 1, 4);

        Button btnSubmit = createMenuButton("⚡ ACTIVATE QUANTUM ACCOUNT", "primary");
        Button btnBack = createMenuButton("↩ BACK TO ORBIT", "tertiary");

        btnSubmit.setOnAction(e -> {
            try {
                String username = tfUsername.getText();
                String password = pfPassword.getText();
                String fullName = tfFullName.getText();
                String email = tfEmail.getText();
                BigDecimal deposit = new BigDecimal(tfDeposit.getText());

                if (deposit.compareTo(new BigDecimal("100000")) < 0) {
                    showAlert("Quantum Error", "Initial fuel must be at least Rp 100.000", Alert.AlertType.ERROR);
                    return;
                }

                auth.buatAkun(username, password, fullName, email, deposit);
                showAlert("Quantum Success", "Account activated! Welcome to Neo-Stock.", Alert.AlertType.INFORMATION);
                showMainMenu();

            } catch (Exception ex) {
                showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnBack.setOnAction(e -> showMainMenu());

        formContainer.getChildren().addAll(title, form, btnSubmit, btnBack);
        rootLayout.setCenter(formContainer);
    }

    private void showLoginForm() {
        VBox formContainer = new VBox(20);
        formContainer.setAlignment(Pos.CENTER);
        formContainer.setPadding(new Insets(40));
        formContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a12, #151528);");

        Label title = new Label("QUANTUM LOGIN");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);

        TextField tfUsername = createStyledTextField();
        PasswordField pfPassword = createStyledPasswordField();

        form.add(createFormLabel("Quantum ID:"), 0, 0);
        form.add(tfUsername, 1, 0);
        form.add(createFormLabel("Security Code:"), 0, 1);
        form.add(pfPassword, 1, 1);

        Button btnLogin = createMenuButton("⚡ INITIATE QUANTUM LINK", "primary");
        Button btnBack = createMenuButton("↩ BACK TO ORBIT", "tertiary");

        btnLogin.setOnAction(e -> {
            try {
                akunAktif = auth.login(tfUsername.getText(), pfPassword.getText());
                showTradingDashboard();
            } catch (Exception ex) {
                showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        btnBack.setOnAction(e -> showMainMenu());

        formContainer.getChildren().addAll(title, form, btnLogin, btnBack);
        rootLayout.setCenter(formContainer);
    }

    private void showTradingDashboard() {
        BorderPane dashboard = new BorderPane();
        dashboard.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a12, #151528);");

        // Top header
        HBox header = createHeader();
        dashboard.setTop(header);

        // Center content - Stock market data
        TabPane contentTabs = createContentTabs();
        dashboard.setCenter(contentTabs);

        rootLayout.setCenter(dashboard);
    }

    private HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setStyle("-fx-background-color: rgba(26, 26, 46, 0.8); -fx-border-color: #444477; -fx-border-width: 0 0 1 0;");

        Label welcomeLabel = new Label("QUANTUM OPERATOR: " + (akunAktif != null ? akunAktif.getNamaLengkap().toUpperCase() : "GUEST"));
        welcomeLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #00ff88; -fx-font-size: 14px; -fx-font-weight: bold;");

        balanceLabel = new Label("QUANTUM FUEL: Rp " + (akunAktif != null ? String.format("%,.2f", akunAktif.getSaldo()) : "0"));
        balanceLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #00ccff; -fx-font-size: 14px; -fx-font-weight: bold;");

        boolean isOpen = (marketService != null) && marketService.isPasarBuka();

        Label marketStatus = new Label(isOpen ? "🟢 MARKET: QUANTUM ACTIVE" : "🔴 MARKET: QUANTUM OFFLINE");
        marketStatus.setStyle(isOpen ?
                "-fx-font-family: 'Segoe UI'; -fx-text-fill: #00ff88; -fx-font-size: 12px; -fx-font-weight: bold;" :
                "-fx-font-family: 'Segoe UI'; -fx-text-fill: #ff4444; -fx-font-size: 12px; -fx-font-weight: bold;");

        Button btnLogout = createMenuButton("⏻ LOGOUT", "danger");
        btnLogout.setOnAction(e -> {
            akunAktif = null;
            showMainMenu();
        });

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        if (akunAktif != null) {
            header.getChildren().addAll(welcomeLabel, balanceLabel, marketStatus, btnLogout);
        } else {
            Button btnLogin = createMenuButton("🔐 LOGIN", "primary");
            btnLogin.setOnAction(e -> showLoginForm());
            header.getChildren().addAll(welcomeLabel, balanceLabel, marketStatus, btnLogin);
        }
        return header;
    }

    private TabPane createContentTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        Tab tabStocks = new Tab("📈 QUANTUM MARKET");
        tabStocks.setContent(createStockMarketView());
        tabStocks.setClosable(false);

        Tab tabPortfolio = new Tab("💼 QUANTUM PORTFOLIO");
        tabPortfolio.setContent(createPortfolioView());
        tabPortfolio.setClosable(false);

        Tab tabTrade = new Tab("⚡ QUANTUM TRADE");
        tabTrade.setContent(createTradeView());
        tabTrade.setClosable(false);

        Tab tabHistory = new Tab("📊 QUANTUM HISTORY");
        tabHistory.setContent(createHistoryView());
        tabHistory.setClosable(false);

        tabPane.getTabs().addAll(tabStocks, tabPortfolio, tabTrade, tabHistory);
        return tabPane;
    }

    private VBox createStockMarketView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("REAL-TIME QUANTUM MARKET DATA");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        marketTable = new TableView<>();
        marketTable.getStyleClass().add("futuristic-table");
        VBox.setVgrow(marketTable, Priority.ALWAYS);

        TableColumn<Saham, String> colKode = new TableColumn<>("Kode");
        colKode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKode()));

        TableColumn<Saham, String> colNama = new TableColumn<>("Nama");
        colNama.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaSaham()));
        colNama.setPrefWidth(200);

        TableColumn<Saham, String> colSektor = new TableColumn<>("Sektor");
        colSektor.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSektor()));

        TableColumn<Saham, BigDecimal> colHarga = new TableColumn<>("Harga");
        colHarga.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHargaSekarang()));
        colHarga.setCellFactory(column -> new TableCell<Saham, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(IDR_FORMAT.format(item));
                    setStyle("-fx-text-fill: #00ccff; -fx-font-weight: bold;");
                }
            }
        });

        TableColumn<Saham, BigDecimal> colPerubahan = new TableColumn<>("Perubahan");
        colPerubahan.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPerubahan()));
        colPerubahan.setCellFactory(column -> new TableCell<Saham, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String sign = item.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
                    setText(String.format("%s%.2f%%", sign, item));
                    if (item.compareTo(BigDecimal.ZERO) >= 0) {
                        setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        marketTable.getColumns().addAll(colKode, colNama, colSektor, colHarga, colPerubahan);
        marketTable.setItems(FXCollections.observableArrayList(marketService.getAllSaham()));

        content.getChildren().addAll(title, marketTable);
        return content;
    }

    private VBox createPortfolioView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        if (akunAktif == null || akunAktif.getPortfolio().isEmpty()) {
            Label emptyLabel = new Label("🚀 NO QUANTUM ASSETS DETECTED\nInitiate your first trade to begin portfolio construction.");
            emptyLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 40;");
            content.getChildren().add(emptyLabel);
            return content;
        }

        Label title = new Label("QUANTUM PORTFOLIO ANALYSIS");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        portfolioTable = new TableView<>();
        portfolioTable.getStyleClass().add("futuristic-table");
        VBox.setVgrow(portfolioTable, Priority.ALWAYS);

        TableColumn<Portfolio, String> colTicker = new TableColumn<>("Ticker");
        colTicker.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKodeSaham()));

        TableColumn<Portfolio, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNamaSaham()));
        colName.setPrefWidth(200);

        TableColumn<Portfolio, Integer> colLot = new TableColumn<>("Lot");
        colLot.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getJumlah() / 100).asObject());

        TableColumn<Portfolio, BigDecimal> colAvgPrice = new TableColumn<>("Avg Price");
        colAvgPrice.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getHargaBeli()));
        colAvgPrice.setCellFactory(column -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(IDR_FORMAT.format(item));
                    setStyle("-fx-text-fill: white;");
                }
            }
        });

        // Current Price (Dynamic)
        TableColumn<Portfolio, BigDecimal> colCurrentPrice = new TableColumn<>("Current Price");
        colCurrentPrice.setCellValueFactory(data -> {
            try {
                Saham saham = marketService.getSaham(data.getValue().getKodeSaham());
                return new SimpleObjectProperty<>(saham.getHargaSekarang());
            } catch (Exception e) {
                return new SimpleObjectProperty<>(BigDecimal.ZERO);
            }
        });
        colCurrentPrice.setCellFactory(column -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(IDR_FORMAT.format(item));
                    setStyle("-fx-text-fill: #00ccff; -fx-font-weight: bold;");
                }
            }
        });

        // Profit/Loss (Dynamic)
        TableColumn<Portfolio, BigDecimal> colProfit = new TableColumn<>("Profit/Loss");
        colProfit.setCellValueFactory(data -> {
            try {
                Saham saham = marketService.getSaham(data.getValue().getKodeSaham());
                return new SimpleObjectProperty<>(data.getValue().hitungKeuntungan(saham.getHargaSekarang()));
            } catch (Exception e) {
                return new SimpleObjectProperty<>(BigDecimal.ZERO);
            }
        });
        colProfit.setCellFactory(column -> new TableCell<Portfolio, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(IDR_FORMAT.format(item));
                    if (item.compareTo(BigDecimal.ZERO) >= 0) {
                        setStyle("-fx-text-fill: #00ff88; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold;");
                    }
                }
            }
        });

        portfolioTable.getColumns().addAll(colTicker, colName, colLot, colAvgPrice, colCurrentPrice, colProfit);
        portfolioTable.setItems(FXCollections.observableArrayList(akunAktif.getPortfolio().values()));

        content.getChildren().addAll(title, portfolioTable);
        return content;
    }

    private VBox createTradeView() {
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

        TextField quantityField = createStyledTextField();
        quantityField.setPromptText("Quantity (lots)");

        Label priceLabel = new Label("Quantum Price: --");
        priceLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px;");

        Label totalLabel = new Label("Total Energy: --");
        totalLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px;");

        Button executeBtn = createMenuButton("⚡ EXECUTE " + type + " ORDER",
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
        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
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
        });

        executeBtn.setOnAction(e -> {
            if (akunAktif == null) {
                showAlert("Quantum Error", "Please login to trade", Alert.AlertType.ERROR);
                return;
            }

            String selectedStock = stockSelector.getValue();
            String quantityText = quantityField.getText();

            if (selectedStock == null || quantityText.isEmpty()) {
                showAlert("Quantum Error", "Please select stock and enter quantity", Alert.AlertType.ERROR);
                return;
            }

            try {
                int lot = Integer.parseInt(quantityText);
                int jumlahLembar = lot * 100;

                TradeResult result;
                if (type.equals("BUY")) {
                    result = tradingService.buyStock(akunAktif, selectedStock, jumlahLembar);
                } else {
                    result = tradingService.sellStock(akunAktif, selectedStock, jumlahLembar);
                }

                if (result.isSuccess()) {
                    akunAktif = result.getUpdatedAccount();
                    showAlert("Quantum Success", result.getMessage(), Alert.AlertType.INFORMATION);

                    // Update balance display
                    if (balanceLabel != null) {
                        balanceLabel.setText("QUANTUM FUEL: Rp " + String.format("%,.2f", akunAktif.getSaldo()));
                    }

                    // Clear form
                    stockSelector.setValue(null);
                    quantityField.clear();
                    priceLabel.setText("Quantum Price: --");
                    totalLabel.setText("Total Energy: --");

                    // Refresh dashboard to update portfolio and balances after trade.
                    refreshDashboard();

                } else {
                    showAlert("Quantum Error", result.getMessage(), Alert.AlertType.ERROR);
                }

            } catch (NumberFormatException ex) {
                showAlert("Quantum Error", "Invalid quantity format", Alert.AlertType.ERROR);
            } catch (Exception ex) {
                showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        section.getChildren().addAll(title, stockSelector, quantityField, priceLabel, totalLabel, executeBtn);
        return section;
    }

    private void refreshDashboard() {
        // Simple way to refresh: rebuild the center content
        TabPane contentTabs = createContentTabs();
        ((BorderPane) rootLayout.getCenter()).setCenter(contentTabs);
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

    private ScrollPane createHistoryView() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label title = new Label("QUANTUM TRANSACTION HISTORY");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        if (akunAktif == null || akunAktif.getRiwayatTransaksi().isEmpty()) {
            Label emptyLabel = new Label("📊 NO TRANSACTION HISTORY\nYour transactions will appear here.");
            emptyLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #6666cc; -fx-font-size: 16px; -fx-text-alignment: center; -fx-padding: 40;");
            content.getChildren().addAll(title, emptyLabel);
            return new ScrollPane(content);
        }

        // Create a simple list view for history
        ListView<String> historyList = new ListView<>();
        historyList.setStyle("-fx-background-color: transparent; -fx-border-color: #444477; -fx-border-radius: 8;");

        ArrayList<Transaksi> riwayat = akunAktif.getRiwayatTransaksi();
        for (int i = riwayat.size() - 1; i >= 0 && i >= riwayat.size() - 20; i--) {
            historyList.getItems().add(riwayat.get(i).toString());
        }

        content.getChildren().addAll(title, historyList);
        return new ScrollPane(content);
    }

    private void showGuestDashboard() {
        // Create a simple guest view
        VBox guestView = new VBox(20);
        guestView.setAlignment(Pos.CENTER);
        guestView.setPadding(new Insets(40));
        guestView.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a0a12, #151528);");

        Label title = new Label("GUEST MODE - MARKET OVERVIEW");
        title.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: linear-gradient(to right, #00ff88, #00ccff);");

        Label info = new Label("Viewing market data in read-only mode.\nRegister or login to start trading.");
        info.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 14px; -fx-text-alignment: center;");

        Button btnRegister = createMenuButton("🆕 REGISTER NOW", "primary");
        Button btnLogin = createMenuButton("🔐 LOGIN", "secondary");
        Button btnBack = createMenuButton("↩ BACK", "tertiary");

        btnRegister.setOnAction(e -> showRegistrationForm());
        btnLogin.setOnAction(e -> showLoginForm());
        btnBack.setOnAction(e -> showMainMenu());

        // Show market data
        ListView<String> marketList = new ListView<>();
        marketList.setStyle("-fx-background-color: transparent; -fx-border-color: #444477; -fx-border-radius: 8;");
        marketList.setPrefHeight(300);

        for (Saham saham : marketService.getAllSaham()) {
            String stockInfo = String.format("%-8s %-25s %-15s Rp %,10.2f %s %s",
                    saham.getKode(), saham.getNamaSaham(), saham.getSektor(),
                    saham.getHargaSekarang(), saham.getStatusWarna(), saham.getPerubahanFormatted());
            marketList.getItems().add(stockInfo);
        }

        guestView.getChildren().addAll(title, info, marketList, btnRegister, btnLogin, btnBack);
        rootLayout.setCenter(guestView);
    }

    private TextField createStyledTextField() {
        TextField tf = new TextField();
        tf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return tf;
    }

    private PasswordField createStyledPasswordField() {
        PasswordField pf = new PasswordField();
        pf.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white; -fx-prompt-text-fill: #6666aa; -fx-padding: 10 15; -fx-font-family: 'Segoe UI';");
        return pf;
    }

    private Label createFormLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: #8888ff; -fx-font-size: 12px; -fx-font-weight: bold;");
        return label;
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #1a1a2e; -fx-border-color: #444477; -fx-border-radius: 4; -fx-background-radius: 4; -fx-text-fill: white;");

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
