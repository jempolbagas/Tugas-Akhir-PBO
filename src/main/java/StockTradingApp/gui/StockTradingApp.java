package main.java.StockTradingApp.gui;

import java.math.BigDecimal;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
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
import main.java.StockTradingApp.service.AuthService;
import main.java.StockTradingApp.service.DataManager;
import main.java.StockTradingApp.service.MarketService;
import main.java.StockTradingApp.service.TradingService;
import main.java.StockTradingApp.service.UserRepository;

public class StockTradingApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private AuthService auth;
    private MarketService marketService;
    private TradingService tradingService;
    private Akun akunAktif = null;
    private Label balanceLabel;

    // Track the active MarketView to handle cleanup
    private MarketView activeMarketView;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("NeoStock - Futuristic Trading Platform");

        try {
            DataManager dataManager = new DataManager();
            UserRepository userRepository = new UserRepository(dataManager);
            auth = new AuthService(userRepository);
            marketService = new MarketService();
            tradingService = new TradingService(marketService, auth);

            // Show notifications if any
            List<String> notifications = auth.getSystemNotifications();
            if (!notifications.isEmpty()) {
                GUIUtils.showAlert("System Notification", String.join("\n", notifications), Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            GUIUtils.showAlert("Critical Error", "Failed to load or save data: " + e.getMessage(), Alert.AlertType.ERROR);
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

        Button btnRegister = GUIUtils.createMenuButton("🆕 QUANTUM REGISTER", "primary");
        Button btnLogin = GUIUtils.createMenuButton("🔐 QUANTUM LOGIN", "secondary");
        Button btnGuest = GUIUtils.createMenuButton("👁️ GUEST MODE", "tertiary");
        Button btnExit = GUIUtils.createMenuButton("⏻ EXIT SYSTEM", "danger");

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

        TextField tfUsername = GUIUtils.createStyledTextField();
        PasswordField pfPassword = GUIUtils.createStyledPasswordField();
        TextField tfFullName = GUIUtils.createStyledTextField();
        TextField tfEmail = GUIUtils.createStyledTextField();
        TextField tfDeposit = GUIUtils.createStyledTextField();

        form.add(GUIUtils.createFormLabel("Quantum ID:"), 0, 0);
        form.add(tfUsername, 1, 0);
        form.add(GUIUtils.createFormLabel("Security Code:"), 0, 1);
        form.add(pfPassword, 1, 1);
        form.add(GUIUtils.createFormLabel("Operator Name:"), 0, 2);
        form.add(tfFullName, 1, 2);
        form.add(GUIUtils.createFormLabel("Quantum Mail:"), 0, 3);
        form.add(tfEmail, 1, 3);
        form.add(GUIUtils.createFormLabel("Initial Fuel:"), 0, 4);
        form.add(tfDeposit, 1, 4);

        Button btnSubmit = GUIUtils.createMenuButton("⚡ ACTIVATE QUANTUM ACCOUNT", "primary");
        Button btnBack = GUIUtils.createMenuButton("↩ BACK TO ORBIT", "tertiary");

        btnSubmit.setOnAction(e -> {
            try {
                String username = tfUsername.getText();
                String password = pfPassword.getText();
                String fullName = tfFullName.getText();
                String email = tfEmail.getText();
                BigDecimal deposit = new BigDecimal(tfDeposit.getText());

                if (deposit.compareTo(new BigDecimal("100000")) < 0) {
                    GUIUtils.showAlert("Quantum Error", "Initial fuel must be at least Rp 100.000", Alert.AlertType.ERROR);
                    return;
                }

                auth.createAccount(username, password, fullName, email, deposit);
                GUIUtils.showAlert("Quantum Success", "Account activated! Welcome to Neo-Stock.", Alert.AlertType.INFORMATION);
                showMainMenu();

            } catch (Exception ex) {
                GUIUtils.showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
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

        TextField tfUsername = GUIUtils.createStyledTextField();
        PasswordField pfPassword = GUIUtils.createStyledPasswordField();

        form.add(GUIUtils.createFormLabel("Quantum ID:"), 0, 0);
        form.add(tfUsername, 1, 0);
        form.add(GUIUtils.createFormLabel("Security Code:"), 0, 1);
        form.add(pfPassword, 1, 1);

        Button btnLogin = GUIUtils.createMenuButton("⚡ INITIATE QUANTUM LINK", "primary");
        Button btnBack = GUIUtils.createMenuButton("↩ BACK TO ORBIT", "tertiary");

        btnLogin.setOnAction(e -> {
            try {
                akunAktif = auth.login(tfUsername.getText(), pfPassword.getText());
                showTradingDashboard();
            } catch (Exception ex) {
                GUIUtils.showAlert("Quantum Error", ex.getMessage(), Alert.AlertType.ERROR);
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

        Button btnLogout = GUIUtils.createMenuButton("⏻ LOGOUT", "danger");
        btnLogout.setOnAction(e -> {
            akunAktif = null;
            // Clean up market view if it exists
            if (activeMarketView != null) {
                activeMarketView.dispose();
                activeMarketView = null;
            }
            showMainMenu();
        });

        HBox.setHgrow(welcomeLabel, Priority.ALWAYS);
        if (akunAktif != null) {
            header.getChildren().addAll(welcomeLabel, balanceLabel, marketStatus, btnLogout);
        } else {
            Button btnLogin = GUIUtils.createMenuButton("🔐 LOGIN", "primary");
            btnLogin.setOnAction(e -> showLoginForm());
            header.getChildren().addAll(welcomeLabel, balanceLabel, marketStatus, btnLogin);
        }
        return header;
    }

    private TabPane createContentTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Dispose previous MarketView to ensure listeners are removed
        if (activeMarketView != null) {
            activeMarketView.dispose();
        }

        // Create new MarketView
        activeMarketView = new MarketView(marketService);
        Tab tabStocks = new Tab("📈 QUANTUM MARKET");
        tabStocks.setContent(activeMarketView.getView());
        tabStocks.setClosable(false);

        Tab tabPortfolio = new Tab("💼 QUANTUM PORTFOLIO");
        tabPortfolio.setContent(new PortfolioView(akunAktif, marketService).getView());
        tabPortfolio.setClosable(false);

        Tab tabTrade = new Tab("⚡ QUANTUM TRADE");
        tabTrade.setContent(new TradeView(akunAktif, marketService, tradingService, () -> {
            // Callback when trade is successful
            if (balanceLabel != null && akunAktif != null) {
                balanceLabel.setText("QUANTUM FUEL: Rp " + String.format("%,.2f", akunAktif.getSaldo()));
            }
            refreshDashboard();
        }).getView());
        tabTrade.setClosable(false);

        Tab tabHistory = new Tab("📊 QUANTUM HISTORY");
        tabHistory.setContent(new HistoryView(akunAktif).getView());
        tabHistory.setClosable(false);

        tabPane.getTabs().addAll(tabStocks, tabPortfolio, tabTrade, tabHistory);
        return tabPane;
    }

    private void refreshDashboard() {
        // Simple way to refresh: rebuild the center content
        TabPane contentTabs = createContentTabs();
        ((BorderPane) rootLayout.getCenter()).setCenter(contentTabs);
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

        Button btnRegister = GUIUtils.createMenuButton("🆕 REGISTER NOW", "primary");
        Button btnLogin = GUIUtils.createMenuButton("🔐 LOGIN", "secondary");
        Button btnBack = GUIUtils.createMenuButton("↩ BACK", "tertiary");

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

    public static void main(String[] args) {
        launch(args);
    }
}
