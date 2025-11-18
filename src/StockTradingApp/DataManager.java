package StockTradingApp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;

public class DataManager {
    private static final String FILE_PATH = "neostock.json";
    private Gson gson;

    public DataManager() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        this.gson = gsonBuilder.setPrettyPrinting().create();
    }

    public void saveData(HashMap<String, Akun> data) throws IOException {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(data, writer);
        }
    }

    public HashMap<String, Akun> loadData() throws IOException {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type type = new TypeToken<HashMap<String, Akun>>(){}.getType();
            return gson.fromJson(reader, type);
        }
    }
}
