package main.java.StockTradingApp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import main.java.StockTradingApp.model.Akun;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "neostock.json";
    private static final String TEMP_FILE_PATH = DATA_DIR + File.separator + "neostock.json.tmp";
    private Gson gson;

    public DataManager() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        this.gson = gsonBuilder.setPrettyPrinting().create();

        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Gagal membuat direktori data: " + DATA_DIR);
            }
        }
    }

    /**
     * Returns the file path used for the database file.
     * @return the full path to the database file
     */
    public static String getFilePath() {
        return FILE_PATH;
    }

    public void saveData(Map<String, Akun> data) throws IOException {
        File tempFile = new File(TEMP_FILE_PATH);
        File finalFile = new File(FILE_PATH);
        try (FileWriter writer = new FileWriter(tempFile)) {
            gson.toJson(data, writer);
        }
        Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    public Map<String, Akun> loadData() throws IOException, JsonSyntaxException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new java.io.FileNotFoundException("File database belum dibuat");
        }

        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<HashMap<String, Akun>>(){}.getType();
            return gson.fromJson(reader, type);
        }
    }
}
