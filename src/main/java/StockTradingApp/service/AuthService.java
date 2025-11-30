package main.java.StockTradingApp.service;

import main.java.StockTradingApp.exception.AkunTidakDitemukanException;
import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.exception.DuplicateUsernameException;
import main.java.StockTradingApp.exception.InvalidPasswordException;
import main.java.StockTradingApp.exception.InvalidUsernameException;
import main.java.StockTradingApp.exception.PasswordSalahException;
import main.java.StockTradingApp.model.Akun;

import java.math.BigDecimal;
import java.util.List;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveData() throws DatabaseSaveException {
        userRepository.saveData();
    }

    public void createAccount(String username, String password, String namaLengkap,
                              String email, BigDecimal saldoAwal)
            throws InvalidUsernameException, InvalidPasswordException, DuplicateUsernameException, DatabaseSaveException {
        if (username.length() < 4) {
            throw new InvalidUsernameException("Username minimal 4 karakter!");
        }

        if (password.length() < 6) {
            throw new InvalidPasswordException("Password minimal 6 karakter!");
        }

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username sudah digunakan!");
        }

        Akun newAccount = new Akun(username, password, namaLengkap, email, saldoAwal);
        userRepository.createAccount(newAccount);
    }

    public Akun login(String username, String password)
            throws AkunTidakDitemukanException, PasswordSalahException {
        Akun account = userRepository.findByUsername(username)
                .orElseThrow(() -> new AkunTidakDitemukanException("Akun tidak ditemukan!"));

        if (!account.getPassword().equals(password)) {
            throw new PasswordSalahException("Password salah!");
        }

        return account;
    }

    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public List<String> getSystemNotifications() {
        return userRepository.getNotifications();
    }
}
