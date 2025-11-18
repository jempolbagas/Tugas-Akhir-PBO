package StockTradingApp;

import java.io.IOException;
import com.google.gson.JsonSyntaxException;
import java.io.File;

class SistemAutentikasi {
    private java.util.HashMap<String, Akun> database;
    private DataManager dataManager;
    
    public SistemAutentikasi() {
        this.dataManager = new DataManager();
        try {
            this.database = dataManager.loadData();
        } catch (java.io.FileNotFoundException e) {
            UIHelper.showNotification("File data tidak ditemukan. Membuat file baru.");
            this.database = new java.util.HashMap<>();
            saveData();
        } catch (JsonSyntaxException e) {
            UIHelper.showNotification("File data rusak. Membuat backup dan memulai dengan data baru.");
            backupCorruptedData();
            this.database = new java.util.HashMap<>();
        } catch (IOException e) {
            UIHelper.showErrorAndExit("Gagal memuat data karena kesalahan I/O.", e);
        }
    }

    private void backupCorruptedData() {
        File source = new File("neostock.json");
        File dest = new File("neostock.json.corrupted." + System.currentTimeMillis());
        if (source.exists()) {
            source.renameTo(dest);
        }
    }

    public void saveData() {
        try {
            dataManager.saveData(database);
        } catch (IOException e) {
            UIHelper.showErrorAndExit("Gagal menyimpan data.", e);
        }
    }
    
    public void buatAkun(String username, String password, String namaLengkap, 
                         String email, double saldoAwal) throws Exception {
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
}