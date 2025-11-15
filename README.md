# NeoStock - Futuristic Trading Platform

A stock trading simulation application designed for beginners to learn the fundamentals of stock trading in a risk-free environment.

## About the Project

NeoStock is a JavaFX-based desktop application that provides a simulated stock trading experience. It is designed to be an educational tool for individuals who are new to the stock market, allowing them to practice buying and selling stocks without using real money. The application features a futuristic, user-friendly interface to make learning engaging and intuitive.

## Key Features

*   **User Authentication:** Secure registration and login system for users.
*   **Simulated Stock Market:** A dynamic market with a variety of stocks whose prices are updated in real-time.
*   **Portfolio Management:** Users can view their stock holdings, track their performance, and see their overall portfolio value.
*   **Trading:** Ability to buy and sell stocks in lot sizes (1 lot = 100 shares).
*   **Guest Mode:** Allows users to view the stock market without creating an account.
*   **Transaction History:** Users can view a history of their past transactions.

## Project Structure

```
.
├── bin/
│   └── StockTradingApp/
├── lib/
│   ├── ... (JavaFX libraries)
└── src/
    └── StockTradingApp/
        ├── Akun.java
        ├── App.java
        ├── CustomExceptions.java
        ├── LaporanManager.java
        ├── PasarSaham.java
        ├── Portofolio.java
        ├── Saham.java
        ├── SistemAutentikasi.java
        ├── SistemTradingSaham.java
        ├── StockTradingApp.java
        ├── Transaksi.java
        ├── UIHelper.java
        └── styles.css
```

*   **`src/`**: Contains all the Java source code for the application, as well as the CSS stylesheet.
*   **`bin/`**: The output directory for the compiled Java class files.
*   **`lib/`**: Contains the JavaFX libraries required to run the application.

## Application Architecture

The application is built using a monolithic architecture, with a clear separation of concerns between the UI and the back-end logic.

*   **`StockTradingApp.java`**: The main class that launches the JavaFX application. It is responsible for creating the UI, handling user input, and coordinating the other classes.
*   **`SistemAutentikasi.java`**: Handles user authentication, including registration and login. It stores user data in a `HashMap`.
*   **`PasarSaham.java`**: Represents the stock market. It manages the list of available stocks and updates their prices periodically.
*   **`Akun.java`**: Represents a user account. It stores the user's personal information, balance, and portfolio.
*   **`Portofolio.java`**: Represents a user's stock portfolio. It keeps track of the stocks the user owns, the number of shares, and the purchase price.
*   **`Saham.java`**: Represents a stock. It stores the stock's code, name, sector, and current price.
*   **`Transaksi.java`**: Represents a transaction (buy or sell). It stores the details of the transaction, such as the stock, number of shares, and price.
*   **`UIHelper.java`**: A utility class that provides helper methods for creating UI components. This class is not yet implemented.
*   **`CustomExceptions.java`**: Contains custom exception classes used throughout the application.

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Java Development Kit (JDK) 8 or higher
*   JavaFX SDK

### Installation & Running

1.  **Clone the repo**
    ```sh
    git clone https://github.com/your_username_/Project-Name.git
    ```
2.  **Navigate to the `src` directory**
    ```sh
    cd Project-Name/src
    ```
3.  **Compile the Java code**
    ```sh
    javac --module-path /path/to/your/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d ../bin StockTradingApp/*.java
    ```
4.  **Run the application**
    ```sh
    java --module-path /path/to/your/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp ../bin StockTradingApp.StockTradingApp
    ```

## Things to Work On

This project is still in its early stages, and there are several areas that need improvement. Contributions in these areas are welcome!

*   **Data Persistence:** Currently, all user data (accounts, portfolios, transaction history) is stored in memory and is lost when the application is closed. A database or file-based persistence mechanism is needed.
*   **UI/UX Improvements:**
    *   The UI is built programmatically. Migrating to FXML would make the UI code cleaner and easier to maintain.
    *   The UI is not responsive and may not look good on all screen sizes.
*   **Error Handling:** The error handling is basic. A more robust error handling system would improve the user experience.
*   **Unit Tests:** There are no unit tests. Adding unit tests would improve the code quality and make it easier to add new features without introducing bugs.
*   **Concurrency:** The stock price update thread could potentially cause concurrency issues. The application should be reviewed for thread safety.

## Future Improvements

Here are some ideas for new features that could be added to the project:

*   **Historical Data:** Add charts and graphs to show the historical performance of stocks.
*   **Advanced Order Types:** Implement more advanced order types, such as limit orders and stop-loss orders.
*   **Market News:** Add a news feed that could affect stock prices.
*   **User Profiles:** Allow users to customize their profiles and view their trading statistics.
*   **Gamification:** Add a leaderboard to show the top-performing traders.

## How to Contribute

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request
