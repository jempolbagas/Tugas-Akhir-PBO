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

public class UserRepository {
    private final DataManager dataManager;
    private Map<String, Akun> database;
    private final List<String> notifications;

    public UserRepository(DataManager dataManager) throws DatabaseLoadException, DatabaseSaveException {
        this.dataManager = dataManager;
        this.notifications = Collections.synchronizedList(new ArrayList<>());
        initializeData();
    }

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

    public void saveData() throws DatabaseSaveException {
        try {
            dataManager.saveData(database);
        } catch (IOException e) {
            throw new DatabaseSaveException("Gagal menyimpan data.", e);
        }
    }

    public void createAccount(Akun account) throws DatabaseSaveException {
        database.put(account.getUsername(), account);
        saveData();
    }

    public Optional<Akun> findByUsername(String username) {
        return Optional.ofNullable(database.get(username));
    }

    public boolean existsByUsername(String username) {
        return database.containsKey(username);
    }

    public List<String> getNotifications() {
        List<String> currentNotifications = new ArrayList<>(notifications);
        notifications.clear();
        return currentNotifications;
    }
}
