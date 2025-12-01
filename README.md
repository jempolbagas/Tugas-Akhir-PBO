# NeoStock - Futuristic Trading Platform

NeoStock is a Java-based stock trading simulation application featuring a "Futuristic/Quantum" aesthetic. It allows users to simulate trading in a real-time market environment, manage a portfolio, and track their transaction history.

## Features

*   **Real-time Market Simulation**: Stock prices fluctuate in real-time.
*   **User Accounts**: Secure registration and login system.
*   **Portfolio Management**: Buy and sell stocks, track average buy price, and view unrealized profit/loss.
*   **Transaction History**: Comprehensive log of all trades and top-ups.
*   **Interactive GUI**: A modern, dark-themed JavaFX interface with charts and responsive controls.
*   **Data Persistence**: All data is saved locally using JSON.
*   **Guest Mode**: View live market data without an account.

## Tech Stack

*   **Language**: Java 17
*   **GUI Framework**: JavaFX 17
*   **Build System**: Maven
*   **Data Storage**: JSON (via Gson)
*   **Testing**: JUnit 5, Mockito

## Prerequisites

*   **Java Development Kit (JDK) 17** or higher.
*   **Maven** (for building and running the project).

## Installation

1.  Clone the repository:
    ```bash
    git clone <repository-url>
    cd StockTradingApp
    ```

2.  Build the project using Maven:
    ```bash
    mvn clean compile
    ```

## Usage

### Running the GUI Application

To launch the main graphical user interface:

```bash
mvn javafx:run
```

### Running the CLI (Legacy)

To launch the command-line interface:

```bash
mvn exec:java -Dexec.mainClass="main.java.StockTradingApp.cli.SistemTradingSaham"
```

## Project Structure

The source code is organized as follows:

*   `src/main/java/StockTradingApp/`
    *   `gui/`: JavaFX views and controllers (StockTradingApp, MarketView, PortfolioView, etc.).
    *   `model/`: Data models (Akun, Saham, Portfolio, Transaksi).
    *   `service/`: Business logic and data services (TradingService, MarketService, AuthService).
    *   `cli/`: Command-line interface entry point.
    *   `exception/`: Custom exception classes.

## Development

*   **Documentation**: All public classes and methods are fully documented with Javadoc.
*   **Style**: The UI uses a "Cyberpunk" theme defined in `src/main/java/StockTradingApp/gui/styles.css`.
*   **Threading**: Market updates run on a background thread. UI updates are dispatched to the JavaFX Application Thread.
