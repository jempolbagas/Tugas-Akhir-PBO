package main.java.StockTradingApp.service;

import com.google.gson.JsonSyntaxException;
import main.java.StockTradingApp.exception.DatabaseLoadException;
import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.model.Akun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository class for managing user data storage and retrieval.
 * Abstraction layer over DataManager for type-safe operations.
 */
public class UserRepository {
    private final DataManager dataManager;
    private Map<String, Akun> database;
    private final List<String> notifications;

    /**
     * Constructs a new UserRepository.
     * Initializes data from storage or creates a new database if needed.
     *
     * @param dataManager The data manager instance.
     * @throws DatabaseLoadException if loading fails.
     * @throws DatabaseSaveException if initialization save fails.
     */
    public UserRepository(DataManager dataManager) throws DatabaseLoadException, DatabaseSaveException {
        this.dataManager = dataManager;
        this.notifications = Collections.synchronizedList(new ArrayList<>());
        initializeData();
    }

    /**
     * Initializes the in-memory database from the persistent storage.
     * Handles file not found and corrupted data scenarios.
     *
     * @throws DatabaseLoadException if an unrecoverable error occurs.
     * @throws DatabaseSaveException if saving a fresh/recovered database fails.
     */
    private void initializeData() throws DatabaseLoadException, DatabaseSaveException {
        try {
            this.database = new ConcurrentHashMap<>(dataManager.loadData());
        } catch (java.io.FileNotFoundException e) {
            notifications.add("File data tidak ditemukan. Membuat file baru.");
            this.database = new ConcurrentHashMap<>();
            saveData();
        } catch (JsonSyntaxException e) {
            notifications.add("File data rusak. Membuat backup dan memulai dengan data baru.");
            backupCorruptedData();
            this.database = new ConcurrentHashMap<>();
            saveData();
        } catch (IOException e) {
            throw new DatabaseLoadException("Gagal memuat data karena kesalahan I/O.", e);
        }
    }

    /**
     * Backs up the corrupted database file to avoid data loss.
     */
    private void backupCorruptedData() {
        String filePath = DataManager.getFilePath();

        File source = new File(filePath);
        File dest = new File(filePath + ".corrupted." + System.currentTimeMillis());

        if (source.exists()) {
            boolean renamed = source.renameTo(dest);
            if (renamed) {
                System.out.println("File database rusak berhasil di-backup ke: " + dest.getName());
            } else {
                System.err.println("Gagal mem-backup file database rusak.");
            }
        }
    }

    /**
     * Persists the current in-memory database to storage.
     *
     * @throws DatabaseSaveException if saving fails.
     */
    public void saveData() throws DatabaseSaveException {
        try {
            dataManager.saveData(database);
        } catch (IOException e) {
            throw new DatabaseSaveException("Gagal menyimpan data.", e);
        }
    }

    /**
     * Adds a new account to the repository and saves it.
     *
     * @param account The account to add.
     * @throws DatabaseSaveException if saving fails.
     */
    public void createAccount(Akun account) throws DatabaseSaveException {
        database.put(account.getUsername(), account);
        saveData();
    }

    /**
     * Finds an account by username.
     *
     * @param username The username to search for.
     * @return An Optional containing the Akun if found, or empty.
     */
    public Optional<Akun> findByUsername(String username) {
        return Optional.ofNullable(database.get(username));
    }

    /**
     * Checks if an account exists with the given username.
     *
     * @param username The username to check.
     * @return true if exists, false otherwise.
     */
    public boolean existsByUsername(String username) {
        return database.containsKey(username);
    }

    /**
     * Retrieves and clears any system notifications generated during initialization.
     *
     * @return A list of notification messages.
     */
    public List<String> getNotifications() {
        List<String> currentNotifications = new ArrayList<>(notifications);
        notifications.clear();
        return currentNotifications;
    }
}
