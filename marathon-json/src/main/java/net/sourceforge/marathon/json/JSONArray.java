package net.sourceforge.marathon.json;

import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JSONArray {

    private JsonArray jsonArray;

    public JSONArray(Object string) {
        Gson gson = new Gson();
        String json = gson.toJson(string);
        StringReader reader = new StringReader(json);
        jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
    }

    public JSONArray(String string) {
        StringReader reader = new StringReader(string);
        jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
    }

    
    public JSONArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray() {
        jsonArray = new JsonArray();
    }

    public int length() {
        return jsonArray.size();
    }

    public Object get(int i) {
        return jsonArray.get(i);
    }

    public int getInt(int i) {
        return jsonArray.get(i).getAsInt();
    }

    public JSONObject getJSONObject(int i) {

        return new JSONObject(jsonArray.get(i).getAsJsonObject());
    }

    public String getString(int i) {
        return jsonArray.get(i).getAsString();
    }

    public JSONArray put(boolean value) {
        jsonArray.add(value);
        return this;
    }

    public JSONArray put(double value) {
        jsonArray.add(value);
        return this;
    }

    public JSONArray put(float value) {
        jsonArray.add(value);
        return this;
    }

    public JSONArray put(int value) {
        jsonArray.add(value);
        return this;
    }

    public JSONArray put(long value) {
        jsonArray.add(value);
        return this;
    }

    public JSONArray put(Object value) {
        Gson g = new Gson();
        String json = g.toJson(value);
        JsonElement vElement = JsonParser.parseString(json);
        jsonArray.add(vElement);
        return this;

    }

    public JSONArray put(JSONArray value) {
        jsonArray.add(value.getValue());
        return this;
    }

    public JSONArray put(JSONObject value) {
        jsonArray.add(value.getValue());
        return this;
    }

    public JSONArray put(String value) {
        jsonArray.add(value);
        return this;

    }

    public JsonElement getValue() {
        return jsonArray;
    }

    @Override
    public String toString() {
        return jsonArray.toString();
    }
}
