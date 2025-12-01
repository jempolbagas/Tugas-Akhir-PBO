package main.java.StockTradingApp.service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Gson TypeAdapter for LocalDateTime.
 * Enables serialization and deserialization of LocalDateTime objects to/from JSON strings.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    /**
     * Serializes a LocalDateTime object to a JSON string.
     *
     * @param out   The JsonWriter.
     * @param value The LocalDateTime value to serialize.
     * @throws IOException if an error occurs during writing.
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(value.toString());
    }

    /**
     * Deserializes a JSON string to a LocalDateTime object.
     *
     * @param in The JsonReader.
     * @return The parsed LocalDateTime object.
     * @throws IOException if an error occurs during reading.
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return LocalDateTime.parse(in.nextString());
    }
}
