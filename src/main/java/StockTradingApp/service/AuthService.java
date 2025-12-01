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

/**
 * Service class responsible for authentication and account management.
 * Handles user registration, login, and validation.
 */
public class AuthService {
    private final UserRepository userRepository;

    /**
     * Constructs a new AuthService.
     *
     * @param userRepository The repository for accessing user data.
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Persists the current user data to the storage.
     *
     * @throws DatabaseSaveException if an error occurs during saving.
     */
    public void saveData() throws DatabaseSaveException {
        userRepository.saveData();
    }

    /**
     * Creates a new user account.
     * Validates input and ensures username uniqueness.
     *
     * @param username     The desired username (min 4 chars).
     * @param password     The password (min 6 chars).
     * @param namaLengkap  The user's full name.
     * @param email        The user's email.
     * @param saldoAwal    The initial balance.
     * @throws InvalidUsernameException   If username is too short.
     * @throws InvalidPasswordException   If password is too short.
     * @throws DuplicateUsernameException If username already exists.
     * @throws DatabaseSaveException      If saving the new account fails.
     */
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

    /**
     * Authenticates a user.
     *
     * @param username The username.
     * @param password The password.
     * @return The authenticated Akun object.
     * @throws AkunTidakDitemukanException If the user does not exist.
     * @throws PasswordSalahException      If the password is incorrect.
     */
    public Akun login(String username, String password)
            throws AkunTidakDitemukanException, PasswordSalahException {
        Akun account = userRepository.findByUsername(username)
                .orElseThrow(() -> new AkunTidakDitemukanException("Akun tidak ditemukan!"));

        if (!account.getPassword().equals(password)) {
            throw new PasswordSalahException("Password salah!");
        }

        return account;
    }

    /**
     * Checks if a username is already taken.
     *
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Retrieves system notifications.
     *
     * @return A list of notification strings.
     */
    public List<String> getSystemNotifications() {
        return userRepository.getNotifications();
    }
}
