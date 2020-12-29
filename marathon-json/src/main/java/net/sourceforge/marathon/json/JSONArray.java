package net.sourceforge.marathon.json;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

public class JSONArray {

    private JsonArray jArray;

    public JSONArray(Object string) {
        Gson gson = new Gson();
        String json = gson.toJson(string);
        StringReader reader = new StringReader(json);
        jArray = JsonParser.parseReader(reader).getAsJsonArray();
    }

    public JSONArray(JSONTokener tokener) {
        InputStreamReader inputStreamReader = new InputStreamReader(tokener.getResourceAsStream());
        jArray = JsonParser.parseReader(inputStreamReader).getAsJsonArray();
    }

    public JSONArray(String string) {
        StringReader reader = new StringReader(string);
        jArray = JsonParser.parseReader(reader).getAsJsonArray();
    }

    public JSONArray(JsonArray jsonArray) {
        this.jArray = jsonArray;
    }

    public JSONArray() {
        jArray = new JsonArray();
    }

    public int length() {
        return jArray.size();
    }

    public Object get(int i) {
        Object o = JSONObject.unwrap(jArray.get(i));
        if (o != null) {
            if (o instanceof JsonArray) {
                return new JSONArray((JsonArray) o);
            } else if (o instanceof JsonObject) {
                return new JSONObject((JsonObject) o);
            }
        }
        return o;
    }

    public int getInt(int i) {
        return jArray.get(i).getAsInt();
    }

    public JSONObject getJSONObject(int i) {

        return new JSONObject(jArray.get(i).getAsJsonObject());
    }

    public String getString(int i) {
        return jArray.get(i).getAsString();
    }

    public JSONArray put(boolean value) {
        jArray.add(value);
        return this;
    }

    public JSONArray put(double value) {
        jArray.add(value);
        return this;
    }

    public JSONArray put(float value) {
        jArray.add(value);
        return this;
    }

    public JSONArray put(int value) {
        jArray.add(value);
        return this;
    }

    public JSONArray put(long value) {
        jArray.add(value);
        return this;
    }

    public JSONArray put(Object value) {
        if (value instanceof JSONObject) {
            return put((JSONObject) value);
        } else if (value instanceof JSONArray) {
            return put((JSONArray) value);
        }
        Gson g = new Gson();
        String json = g.toJson(value);
        JsonElement vElement = JsonParser.parseString(json);
        jArray.add(vElement);
        return this;

    }

    public JSONArray put(JSONArray value) {
        jArray.add(value.getValue());
        return this;
    }

    public JSONArray put(JSONObject value) {
        jArray.add(value.getValue());
        return this;
    }

    public JSONArray put(String value) {
        jArray.add(value);
        return this;

    }

    public JsonElement getValue() {
        return jArray;
    }

    @Override
    public String toString() {
        return jArray.toString();
    }

    public double getDouble(int i) {
        return jArray.get(i).getAsDouble();
    }

    public JSONArray getJSONArray(int i) {
        return new JSONArray(jArray.get(i).getAsJsonArray());
    }

    public void remove(int i) {
        jArray.remove(i);
    }

    public String toString(int indentFactor) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        if (indentFactor > 0) {
            for (int i = 1; i <= indentFactor; i++)
                stringBuilder.append(" ");
        }
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.setIndent(stringBuilder.toString());
            jsonWriter.setLenient(true);
            Streams.write(getValue(), jsonWriter);
            return stringWriter.toString();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
