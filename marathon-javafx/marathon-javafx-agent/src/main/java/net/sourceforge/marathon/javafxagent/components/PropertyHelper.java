/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.marathon.json.JSONObject;

public class PropertyHelper {

    public static final Logger LOGGER = Logger.getLogger(PropertyHelper.class.getName());

    public static String toString(Properties p, String[] propOrder) {
        StringBuffer sb = new StringBuffer();

        if (p.size() > 1) {
            sb.append("{");
        }
        char[] convertBuf = new char[1024];
        for (int i = 0; i < propOrder.length; i++) {
            sb.append(escape(p.getProperty(propOrder[i]), convertBuf));
            if (i < propOrder.length - 1) {
                sb.append(", ");
            }
        }
        if (p.size() > 1) {
            sb.append("}");
        }
        return sb.toString();
    }

    private static String escape(String s, char[] convertBuf) {
        if (convertBuf.length < s.length()) {
            int newLen = s.length() * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convertBuf = new char[newLen];
        }
        int convertLen = 0;

        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
            case '{':
            case '}':
            case '\\':
            case ',':
            case ':':
                convertBuf[convertLen++] = '\\';
                break;
            case ' ':
                if (i == 0) {
                    convertBuf[convertLen++] = '\\';
                }
                break;
            default:
                break;
            }
            convertBuf[convertLen++] = chars[i];
        }
        return new String(convertBuf, 0, convertLen);
    }

    public static String toString(Properties[] pa, String[] propOrder) {
        StringBuffer sb = new StringBuffer();

        sb.append("[");
        for (int i = 0; i < pa.length; i++) {
            sb.append(toString(pa[i], propOrder));
            if (i < pa.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private final static class TokenReader {
        final static int ID = 1;
        final static int COMMA = ',';
        final static int OPENBR = '{';
        final static int CLOSEBR = '}';
        final static int COLON = ':';

        private char[] b;
        String text;
        int index;
        private int len;

        public TokenReader(String s) {
            b = s.toCharArray();
            len = b.length;
            index = 0;
        }

        public TokenReader(String s, int off, int len) {
            b = s.toCharArray();
            index = off;
            this.len = len;
        }

        public boolean hasNext() {
            return index < len;
        }

        public int next() {
            skipSpaces();
            if (index >= len) {
                throw new RuntimeException("Invalid property list format");
            }
            switch (b[index]) {
            case ',':
            case '{':
            case '}':
            case ':':
                return b[index++];
            default:
                StringBuffer sb = new StringBuffer();
                while (index < len && b[index] != ',' && b[index] != '{' && b[index] != '}' && b[index] != ':') {
                    if (b[index] == '\\') {
                        index++;
                        if (index >= len) {
                            throw new RuntimeException("Invalid property list format");
                        }
                    }
                    sb.append(b[index]);
                    index++;
                }
                text = sb.toString();
                return ID;
            }
        }

        private void skipSpaces() {
            while (index < len && b[index] == ' ') {
                index++;
            }
        }

        public String getText() {
            return text;
        }
    }

    public static Properties fromString(String s, String[][] props) {
        TokenReader reader = new TokenReader(s);

        return readProperties(reader, props);
    }

    public static Properties readProperties(TokenReader reader, String[][] props) {
        if (!reader.hasNext()) {
            throw new RuntimeException("Invalid property list format");
        }

        int token = reader.next();
        if (token == TokenReader.ID) {
            Properties p = new Properties();
            for (String[] prop : props) {
                if (prop.length == 1) {
                    p.setProperty(prop[0], reader.getText());
                    return p;
                }
            }
            throw new RuntimeException("Invalid property list format");
        }
        if (token == TokenReader.OPENBR) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            while (token != TokenReader.CLOSEBR) {
                String[] tokens = new String[2];
                token = reader.next();
                if (token != TokenReader.ID) {
                    throw new RuntimeException("Invalid property list format");
                }
                tokens[0] = reader.getText();
                token = reader.next();
                if (token == TokenReader.COLON) {
                    token = reader.next();
                    if (token != TokenReader.ID) {
                        throw new RuntimeException("Invalid property list format");
                    }
                    tokens[1] = reader.getText();
                    token = reader.next();
                    if (token != TokenReader.COMMA && token != TokenReader.CLOSEBR) {
                        throw new RuntimeException("Invalid property list format");
                    }
                } else if (token == TokenReader.COMMA || token == TokenReader.CLOSEBR) {
                    tokens[1] = tokens[0];
                    tokens[0] = null;
                }
                list.add(tokens);
            }
            String[] first = list.get(0);
            if (first[0] == null) {
                Properties p = new Properties();
                for (String[] selectedProps : props) {
                    if (selectedProps.length == list.size()) {
                        for (int j = 0; j < selectedProps.length; j++) {
                            p.setProperty(selectedProps[j], list.get(j)[1]);
                        }
                        return p;
                    }
                }
                throw new RuntimeException("Invalid property list format");
            }
            Properties p = new Properties();
            for (int i = 0; i < list.size(); i++) {
                String[] prop = list.get(i);
                p.setProperty(prop[0], prop[1]);
            }
            return p;
        }
        throw new RuntimeException("Invalid property list format");
    }

    public static Properties[] fromStringToArray(String s, String[][] props) {
        s = s.trim();
        if (!s.startsWith("[") || !s.endsWith("]")) {
            throw new RuntimeException("Invalid property list format");
        }
        if (s.length() == 2) {
            return new Properties[0];
        }

        ArrayList<Properties> plist = new ArrayList<Properties>();
        int token = TokenReader.COMMA;
        TokenReader reader = new TokenReader(s, 1, s.length() - 1);
        while (token == TokenReader.COMMA && reader.hasNext()) {
            Properties p = readProperties(reader, props);
            plist.add(p);
            if (reader.hasNext()) {
                token = reader.next();
            }
        }
        return plist.toArray(new Properties[plist.size()]);
    }

    public static String toCSS(Properties properties) {
        Entry<Object, Object> typeProperty = null;
        Entry<Object, Object> indexProperty = null;
        Entry<Object, Object> tagNameProperty = null;

        StringBuilder sb = new StringBuilder();
        Set<Entry<Object, Object>> entries = properties.entrySet();
        for (Entry<Object, Object> entry : entries) {
            if (entry.getKey().equals("type")) {
                typeProperty = entry;
            } else if (entry.getKey().equals("indexOfType")) {
                indexProperty = entry;
            } else if (entry.getKey().equals("tagName")) {
                tagNameProperty = entry;
            } else {
                String value = entry.getValue().toString();
                value = value.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
                sb.append("[").append(entry.getKey().toString()).append("=").append("'").append(value).append("']");
            }
        }
        String r = sb.toString();
        if (tagNameProperty != null) {
            r = tagNameProperty.getValue().toString();
        }
        if (typeProperty != null) {
            r = "[" + typeProperty.getKey().toString() + "=" + "'" + typeProperty.getValue().toString() + "']" + sb.toString();
        }
        if (indexProperty != null) {
            int index = Integer.parseInt(indexProperty.getValue().toString());
            r = r + ":nth(" + (index + 1) + ")";
        }
        return r;
    }

    static public class TableSelection {
        private String[] rows;
        private String[] columns;

        @Override
        public String toString() {
            return "TableSelection [rows=" + Arrays.toString(rows) + ", columns=" + Arrays.toString(columns) + "]";
        }

        public TableSelection(String[] rows, String[] columns) {
            this.rows = rows;
            this.columns = columns;
        }

        public String[] getRows() {
            return rows;
        }

        public String[] getColumns() {
            return columns;
        }

    }

    public static TableSelection fromTableSelection(String text) {
        text = text.trim();
        Pattern p = Pattern.compile("rows *: *\\[([^\\]]*)\\] *, *columns *: *\\[([^\\]]*)\\]");
        Matcher matcher = p.matcher(text);
        if (matcher.matches()) {
            TableSelection tableSelection = new TableSelection(parseCSV(matcher.group(1)), parseCSV(matcher.group(2)));
            return tableSelection;
        }
        return null;
    }

    private static String[] parseCSV(String text) {
        ArrayList<String> al = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(text, ",");
        while (tok.hasMoreTokens()) {
            al.add(tok.nextToken());
        }
        return al.toArray(new String[al.size()]);
    }

    public static String toCSS(JSONObject urp) {
        Properties props = new Properties();
        String[] names = JSONObject.getNames(urp);
        for (String prop : names) {
            props.setProperty(prop, urp.get(prop).toString());
        }
        return toCSS(props);
    }

    public static String toCSS(String name) {
        Properties props = new Properties();
        props.setProperty("contextualTitle", name);
        return toCSS(props);
    }

    public static Properties asProperties(JSONObject jsonObject) {
        Properties r = new Properties();
        String[] names = JSONObject.getNames(jsonObject);
        if (names != null) {
            for (String name : names) {
                r.setProperty(name, jsonObject.get(name).toString());
            }
        }
        return r;
    }

}
