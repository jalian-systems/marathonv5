package net.sourceforge.marathon.json;

import java.io.StringReader;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONObject {

    private JsonObject jsonObject;

    public JSONObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject(Object object) {
        Gson g = new Gson();
        String jS = g.toJson(object);
        StringReader reader = new StringReader(jS);
        jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public JSONObject(String string) {
        StringReader reader = new StringReader(string);
        jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public JSONObject() {
        jsonObject = new JsonObject();
    }

    public Object get(String string) {
        return jsonObject.get(string).getAsString();
    }

    public boolean getBoolean(String string) {
        return jsonObject.get(string).getAsBoolean();
    }

    public int getInt(String string) {
        return jsonObject.get(string).getAsInt();
    }

    public JSONArray getJSONArray(String string) {
        return new JSONArray(jsonObject.get(string).getAsJsonArray());
    }

    public JSONObject getJSONObject(String string) {
        return new JSONObject(jsonObject.get(string).getAsJsonObject());
    }

    public long getLong(String string) {
        return jsonObject.get(string).getAsLong();
    }

    public String getString(String string) {
        return jsonObject.get(string).getAsString();
    }

    public boolean has(String string) {
        return jsonObject.has(string);
    }

    public Iterator<String> keys() {
        return jsonObject.keySet().iterator();
    }

    public JSONObject put(String key, boolean value) {
        jsonObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, double value) {
        jsonObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, float value) {
        jsonObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, int value) {
        jsonObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, long value) {
        jsonObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, String value) {
        jsonObject.addProperty(key, value);
        return this;
    }

    public int length() {
        return jsonObject.size();
    }

    public JSONObject put(String key, JSONArray value) {
        jsonObject.add(key, value.getValue());
        return this;
    }

    public JSONObject put(String key, JSONObject put) {
        jsonObject.add(key, put.getValue());
        return this;

    }

    public JsonElement getValue() {
        return jsonObject;
    }

    public JSONObject put(String key, Object value) {
        Gson g = new Gson();
        String json = g.toJson(value);
        JsonElement vElement = JsonParser.parseString(json);
        jsonObject.add(key, vElement);
        return this;
    }

    public static String[] getNames(JSONObject urp) {
        if (urp.isEmpty()) {
            return null;
        }
        return ((JsonObject) urp.getValue()).keySet().toArray(new String[urp.length()]);
    }

    private boolean isEmpty() {
        return jsonObject.size() == 0;
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }

}
