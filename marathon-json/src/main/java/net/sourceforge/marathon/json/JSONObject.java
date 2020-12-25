package net.sourceforge.marathon.json;

import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class JSONObject {

    public static Object NULL = JsonNull.INSTANCE;
    private JsonObject jObject;

    public JSONObject(JsonObject jsonObject) {
        this.jObject = jsonObject;
    }

    public JSONObject(Object object) {
        Gson g = new Gson();
        String jS = g.toJson(object);
        StringReader reader = new StringReader(jS);
        jObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public JSONObject(String string) {
        StringReader reader = new StringReader(string);
        jObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public JSONObject() {
        jObject = new JsonObject();
    }

    public Object get(String string) {
        Object o = JSONObject.unwrap(jObject.get(string));
        if (o != null) {
            if (o instanceof JsonArray) {
                return new JSONArray((JsonArray) o);
            } else if (o instanceof JsonObject) {
                return new JSONObject((JsonObject) o);
            }
        }
        return o;
    }

    public boolean getBoolean(String string) {
        return jObject.get(string).getAsBoolean();
    }

    public int getInt(String string) {
        return jObject.get(string).getAsInt();
    }

    public JSONArray getJSONArray(String string) {
        return new JSONArray(jObject.get(string).getAsJsonArray());
    }

    public JSONObject getJSONObject(String string) {
        return new JSONObject(jObject.get(string).getAsJsonObject());
    }

    public long getLong(String string) {
        return jObject.get(string).getAsLong();
    }

    public String getString(String string) {
        return jObject.get(string).getAsString();
    }

    public boolean has(String string) {
        return jObject.has(string);
    }

    public Iterator<String> keys() {
        return jObject.keySet().iterator();
    }

    public JSONObject put(String key, boolean value) {
        jObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, double value) {
        jObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, float value) {
        jObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, int value) {
        jObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, long value) {
        jObject.addProperty(key, value);
        return this;

    }

    public JSONObject put(String key, String value) {
        jObject.addProperty(key, value);
        return this;
    }

    public int length() {
        return jObject.size();
    }

    public JSONObject put(String key, JSONArray value) {
        jObject.add(key, value.getValue());
        return this;
    }

    public JSONObject put(String key, JSONObject put) {
        jObject.add(key, put.getValue());
        return this;

    }

    public JsonElement getValue() {
        return jObject;
    }

    public JSONObject put(String key, Object value) {
        if (value instanceof JSONArray) {
            return put(key, (JSONArray) value);
        } else if (value instanceof JSONObject) {
            return put(key, (JSONObject) value);
        }
        Gson g = new Gson();
        String json = g.toJson(value);
        JsonElement vElement = JsonParser.parseString(json);
        jObject.add(key, vElement);
        return this;
    }

    public static String[] getNames(JSONObject urp) {
        if (urp.isEmpty()) {
            return null;
        }
        return ((JsonObject) urp.getValue()).keySet().toArray(new String[urp.length()]);
    }

    private boolean isEmpty() {
        return jObject.size() == 0;
    }

    @Override
    public String toString() {
        return jObject.toString();
    }

    public static Object unwrap(final Object o) {
        if (o == null) {
            return null;
        }

        if (!(o instanceof JsonElement)) {
            return o;
        }

        JsonElement e = (JsonElement) o;

        if (e.isJsonNull()) {
            return null;
        } else if (e.isJsonPrimitive()) {

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (p.isString()) {
                return p.getAsString();
            } else if (p.isBoolean()) {
                return p.getAsBoolean();
            } else if (p.isNumber()) {
                return unwrapNumber(p.getAsNumber());
            }
        }

        return o;
    }

    private static boolean isPrimitiveNumber(final Number n) {
        return n instanceof Integer || n instanceof Double || n instanceof Long || n instanceof BigDecimal
                || n instanceof BigInteger;
    }

    private static Number unwrapNumber(final Number n) {
        Number unwrapped;

        if (!isPrimitiveNumber(n)) {
            BigDecimal bigDecimal = new BigDecimal(n.toString());
            if (bigDecimal.scale() <= 0) {
                if (bigDecimal.compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.intValue();
                } else if (bigDecimal.compareTo(new BigDecimal(Long.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.longValue();
                } else {
                    unwrapped = bigDecimal;
                }
            } else {
                final double doubleValue = bigDecimal.doubleValue();
                if (BigDecimal.valueOf(doubleValue).compareTo(bigDecimal) != 0) {
                    unwrapped = bigDecimal;
                } else {
                    unwrapped = doubleValue;
                }
            }
        } else {
            unwrapped = n;
        }
        return unwrapped;
    }

}
