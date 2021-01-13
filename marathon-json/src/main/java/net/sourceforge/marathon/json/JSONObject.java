package net.sourceforge.marathon.json;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

public class JSONObject {

    private static final String CLASS_VALUE_PREFIX = "~~class~~";
    public static Object NULL = JsonNull.INSTANCE;
    private JsonObject jObject;

    public JSONObject() {
        this(new JsonObject());
    }

    JSONObject(JsonObject jsonObject) {
        this.jObject = jsonObject;
    }

    public JSONObject(Object object) {
        Gson g = new Gson();
        String jS = g.toJson(object);
        initJsonObject(jS);
    }

    public JSONObject(String string) {
        initJsonObject(string);
    }

    private void initJsonObject(String jS) {
        StringReader reader = new StringReader(jS);
        jObject = JsonParser.parseReader(reader).getAsJsonObject();
    }

    public Object get(String string) {
        Object o = JSONObject.unwrap(jObject.get(string));
        if (o != null) {
            if (o instanceof JsonArray) {
                return new JSONArray((JsonArray) o);
            } else if (o instanceof JsonObject) {
                return new JSONObject((JsonObject) o);
            } else if (o instanceof String) {
                String s = (String) o;
                if (s.startsWith(CLASS_VALUE_PREFIX)) {
                    try {
                        o = Class.forName(s.substring(CLASS_VALUE_PREFIX.length()));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        o = null;
                    }
                }
            }
        }
        return o;
    }

    public boolean getBoolean(String string) {
        try {
            return jObject.get(string).getAsBoolean();
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    public int getInt(String string) {
        try {
            return jObject.get(string).getAsInt();
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    public JSONArray getJSONArray(String string) {
        try {
            return new JSONArray(jObject.get(string).getAsJsonArray());
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    public JSONObject getJSONObject(String string) {
        try {
            return new JSONObject(jObject.get(string).getAsJsonObject());
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    public long getLong(String string) {
        try {
            return jObject.get(string).getAsLong();
        } catch (Throwable e) {
            throw new JSONException(e);
        }
    }

    public String getString(String string) {
        try {
            return jObject.get(string).getAsString();
        } catch (Throwable e) {
            throw new JSONException(e);
        }
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
        } else if (value instanceof Class<?>) {
            value = CLASS_VALUE_PREFIX + ((Class<?>) value).getName();
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

        if (NULL.equals(o)) {
            return o;
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

    public Set<String> keySet() {
        return jObject.keySet();
    }

    public void remove(String property) {
        jObject.remove(property);
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object object = opt(key);
        if (object == null || NULL.equals(object)) {
            return defaultValue;
        }
        return object.toString();
    }

    public Object opt(String key) {
        return key == null ? null : unwrap(jObject.get(key));
    }

    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        Object val = this.opt(key);
        if (val == null || NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Boolean) {
            return ((Boolean) val).booleanValue();
        }
        try {
            return this.getBoolean(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public double getDouble(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble(object.toString());
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key) + "] is not a number.", e);
        }
    }

    public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.isEmpty()) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }

    public double optDouble(String key, double defaultValue) {
        Object val = this.opt(key);
        if (val == null || NULL.equals(val)) {
            return defaultValue;
        }
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (Exception e) {
                return defaultValue;
            }
        }
        return defaultValue;

    }

    public JSONArray optJSONArray(String key) {
        Object o = this.opt(key);
        if (o == null || NULL.equals(o)) {
            return null;
        }
        return new JSONArray((JsonArray) o);
    }

    public JSONObject optJSONObject(String key) {
        Object o = this.opt(key);
        if (o == null || NULL.equals(o)) {
            return null;
        }
        return new JSONObject((JsonObject) o);
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
