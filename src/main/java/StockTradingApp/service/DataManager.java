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

/**
 * Manages data persistence using JSON files.
 * Handles loading and saving account data to a local file.
 */
public class DataManager {
    private static final String DATA_DIR = "data";
    private static final String FILE_PATH = DATA_DIR + File.separator + "neostock.json";
    private static final String TEMP_FILE_PATH = DATA_DIR + File.separator + "neostock.json.tmp";
    private Gson gson;

    /**
     * Constructs a new DataManager.
     * Initializes the Gson instance and ensures the data directory exists.
     *
     * @throws IOException if the data directory cannot be created.
     */
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

    /**
     * Saves the account data to the JSON file.
     * Uses atomic move to ensure data integrity.
     *
     * @param data The map of account data (Username -> Akun).
     * @throws IOException if an error occurs during writing.
     */
    public void saveData(Map<String, Akun> data) throws IOException {
        File tempFile = new File(TEMP_FILE_PATH);
        File finalFile = new File(FILE_PATH);
        try (FileWriter writer = new FileWriter(tempFile)) {
            gson.toJson(data, writer);
        }
        Files.move(tempFile.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Loads account data from the JSON file.
     *
     * @return A map of account data (Username -> Akun).
     * @throws IOException if the file cannot be read.
     * @throws JsonSyntaxException if the file content is invalid JSON.
     */
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
