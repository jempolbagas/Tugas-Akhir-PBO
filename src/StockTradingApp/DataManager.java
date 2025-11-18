package StockTradingApp;

import java.io.*;

public class DataManager {

    public static void saveData(Object data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Data has been saved to " + filename);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Object loadData(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Data file not found. Starting with a fresh state.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object data = ois.readObject();
            System.out.println("Data has been loaded from " + filename);
            return data;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
