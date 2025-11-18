package StockTradingApp;

import java.io.IOException;

class SistemAutentikasi {
    private java.util.HashMap<String, Akun> database;
    private DataManager dataManager;
    
    public SistemAutentikasi() {
        this.dataManager = new DataManager();
        try {
            this.database = dataManager.loadData();
        } catch (java.io.FileNotFoundException e) {
            UIHelper.showErrorAndExit("File data tidak ditemukan. Aplikasi akan ditutup.", e);
        } catch (IOException e) {
            UIHelper.showErrorAndExit("Gagal memuat data. File mungkin rusak.", e);
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