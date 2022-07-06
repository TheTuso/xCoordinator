package pl.tuso.coordinator.lettuce.messaging;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.UUID;

public class Message {
    private final String TYPE;
    private final UUID id;
    private final HashMap<String, String> params;
    private static final Gson gson = new Gson();

    public Message(String type) {
        this.TYPE = type;
        this.id = UUID.randomUUID();
        this.params = new HashMap<>();
    }

    public Message setParam(String key, String value) {
        if (containsParam(key)) return null;
        this.params.put(key, value);
        return this;
    }

    public Message removeParam(String key) {
        if (!containsParam(key)) return null;
        this.params.remove(key);
        return this;
    }

    public String getParam(String key) {
        return this.params.get(key);
    }

    public String getType() {
        return this.TYPE;
    }

    public UUID getId() {
        return this.id;
    }

    public boolean containsParam(String key) {
        return this.params.containsKey(key);
    }

    public String serialize() {
        return gson.toJson(this);
    }

    public static Message deserialize(String serializedString) {
        return gson.fromJson(serializedString, Message.class);
    }
}
