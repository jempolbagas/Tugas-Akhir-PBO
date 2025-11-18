package StockTradingApp;

class SistemAutentikasi {
    private java.util.HashMap<String, Akun> database;
    private static final String DATA_FILE = "neostock.dat";

    @SuppressWarnings("unchecked")
    public SistemAutentikasi() {
        Object loadedData = DataManager.loadData(DATA_FILE);
        if (loadedData != null && loadedData instanceof java.util.HashMap) {
            this.database = (java.util.HashMap<String, Akun>) loadedData;
        } else {
            this.database = new java.util.HashMap<>();
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
        DataManager.saveData(database, DATA_FILE);
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

    public void saveData() {
        DataManager.saveData(database, DATA_FILE);
    }
}