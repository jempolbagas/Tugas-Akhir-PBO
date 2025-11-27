package main.java.StockTradingApp.service;

import java.io.IOException;
import java.math.BigDecimal;
import com.google.gson.JsonSyntaxException;
import main.java.StockTradingApp.exception.AkunTidakDitemukanException;
import main.java.StockTradingApp.exception.DatabaseLoadException;
import main.java.StockTradingApp.exception.DatabaseSaveException;
import main.java.StockTradingApp.exception.PasswordSalahException;
import main.java.StockTradingApp.model.Akun;

import java.io.File;

public class SistemAutentikasi {
    private java.util.HashMap<String, Akun> database;
    private DataManager dataManager;
    private java.util.List<String> notifications;
    
    public SistemAutentikasi() throws DatabaseLoadException, DatabaseSaveException {
        this.notifications = new java.util.ArrayList<>();
        try {
            this.dataManager = new DataManager();
            this.database = dataManager.loadData();
        } catch (java.io.FileNotFoundException e) {
            notifications.add("File data tidak ditemukan. Membuat file baru.");
            this.database = new java.util.HashMap<>();
            saveData();
        } catch (JsonSyntaxException e) {
            notifications.add("File data rusak. Membuat backup dan memulai dengan data baru.");
            backupCorruptedData();
            this.database = new java.util.HashMap<>();
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
    
    public void buatAkun(String username, String password, String namaLengkap, 
                         String email, BigDecimal saldoAwal) throws Exception {
        if (database.containsKey(username)) {
            throw new Exception("Username sudah digunakan!");
        }
        
        if (username.length() < 4) {
            throw new Exception("Username minimal 4 karakter!");
        }
        
        if (password.length() < 6) {
            throw new Exception("Password minimal 6 karakter!");
        }
        
        Akun akunBaru = new Akun(username, password, namaLengkap, email, saldoAwal);
        database.put(username, akunBaru);
        saveData();
    }
    
    public Akun login(String username, String password) 
            throws AkunTidakDitemukanException, PasswordSalahException {
        if (!database.containsKey(username)) {
            throw new AkunTidakDitemukanException("Akun tidak ditemukan!");
        }
        
        Akun akun = database.get(username);
        if (!akun.getPassword().equals(password)) {
            throw new PasswordSalahException("Password salah!");
        }
        
        return akun;
    }
    
    public boolean isUsernameExist(String username) {
        return database.containsKey(username);
    }

    public java.util.List<String> getNotifications() {
        java.util.List<String> currentNotifications = new java.util.ArrayList<>(notifications);
        notifications.clear();
        return currentNotifications;
    }
}