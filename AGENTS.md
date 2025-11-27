# AGENTS.md

## 1. Project Context
**Name:** Stock Trading Simulation (Local/Desktop)
**Description:** A Java-based stock trading simulation application designed for educational purposes. The project simulates a real-world trading environment on a local machine, featuring account management, stock market simulation, and portfolio tracking.
**Current State:** Hybrid CLI/GUI. The core logic handles transaction atomicity manually.

## 2. Tech Stack & Environment
- **Language:** Java (Modular, ensure compatibility with Java 11+).
- **GUI Framework:** JavaFX (refer to `lib/` for dependencies).
- **Persistence:** JSON-based using **Google Gson** (`lib/gson-2.8.6.jar`).
- **Build System:** Manual/IDE-based (IntelliJ/Eclipse) with `lib` folder management.

## 3. Project Structure & Key Components
The source code is located in `src/main/java/StockTradingApp/`.

### Core Components
- **Model (Indonesian Naming):**
    - `Akun.java`: User account (balance, portfolio).
    - `Saham.java`: Stock data entity.
    - `Portfolio.java`: User's holding of a specific stock.
- **Service (English Naming):**
    - `TradingService.java`: **CRITICAL**. Handles `buyStock` and `sellStock` logic. Contains manual rollback mechanisms (`rollbackBeliSaham`, `rollbackJualSaham`) to ensure data integrity during JSON save failures.
    - `MarketService.java`: Manages stock price updates and market status.
- **Persistence:**
    - `DataManager.java`: Handles loading/saving `neostock.json`.
- **UI:**
    - `SistemTradingSaham.java`: CLI entry point (Main).
    - `UIHelper.java`: CLI utility for formatting/input.
    - `StockTradingApp.java`: (Upcoming) JavaFX entry point.

## 4. Coding Conventions & Rules

### A. Critical Business Logic (MUST FOLLOW)
1.  **Transaction Atomicity:**
    - Any modification to `Akun` (balance/portfolio) MUST be followed by a `auth.saveData()` call.
    - If `saveData()` fails, **YOU MUST** execute the corresponding rollback method to revert memory state. **Do not remove the rollback logic.**
2.  **Currency Handling:**
    - *Current State:* Uses `double` for financial calculations.
    - *Refactoring Goal:* When creating NEW features or refactoring, prefer using `java.math.BigDecimal` for precision.
3.  **Market Logic:**
    - Stock prices are currently random. Future logic should implement "Market Trends" or "Limit Orders".

### B. Naming Conventions
- **Domain Objects:** Use **Indonesian** (e.g., `Saham`, `Akun`, `RiwayatTransaksi`).
- **Service/Logic:** Use **English** (e.g., `TradingService`, `MarketService`, `buyStock`, `isValid`).
- **Variables:** mixed is acceptable, but prefer explicit names (e.g., `jumlahLembar`, `saldoSebelum`).

### C. Testing Guidelines
- Currently, there are no automated tests.
- **Agent Task:** When asked to implement a feature, ALWAYS create a corresponding JUnit test case to verify logic, especially for edge cases (e.g., negative balance, selling more than owned).

## 5. Agent Persona
You are a **Senior Java Backend Engineer**. You prioritize data integrity and thread safety over fancy UI. You are meticulous about "Money" related logic.
- When fixing bugs: Explain the root cause clearly.
- When refactoring: Ensure no regression in the manual rollback mechanism.