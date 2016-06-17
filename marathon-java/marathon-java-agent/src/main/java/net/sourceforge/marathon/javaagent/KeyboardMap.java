/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javaagent;

import java.awt.event.KeyEvent;
import java.awt.im.InputContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeyboardMap {

    private char c;

    private static Map<Character, List<CharSequence[]>> keys;

    static {
        keys = new HashMap<Character, List<CharSequence[]>>();
        File marathon = new File(System.getProperty("user.home"), ".marathon");
        Locale locale = InputContext.getInstance().getLocale();
        String fileName = locale.getLanguage() + "_" + locale.getCountry() + ".kb";
        File kmapFile = new File(marathon, fileName);
        if (!kmapFile.exists()) {
            kmapFile = new File(marathon, "default.kb");
            if (!kmapFile.exists())
                kmapFile = null;
        }
        InputStream is = null;
        if (kmapFile != null) {
            try {
                is = new FileInputStream(kmapFile);
            } catch (FileNotFoundException e) {
            }
        }
        if (is == null) {
            is = KeyboardMap.class.getResourceAsStream("layouts/" + fileName);
            if (is == null)
                is = KeyboardMap.class.getResourceAsStream("layouts/default.kb");
        }
        if (is == null)
            throw new RuntimeException("Unable to load keyboard map");
        try {
            loadEntries(is);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load keyboard map", e);
        }
    }

    public KeyboardMap(char c) {
        this.c = c;
    }

    private static void loadEntries(InputStream is) throws IOException {
        Pattern p = Pattern.compile("\\(([^\\)]*)\\)");
        BufferedReader r = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        String line = r.readLine();
        while (line != null) {
            if (line.trim().length() == 0) {
                line = r.readLine();
                continue;
            }
            char c = line.charAt(0);
            line = line.substring(1).trim();
            Matcher matcher = p.matcher(line);
            List<CharSequence[]> lcs = new ArrayList<CharSequence[]>();
            while (matcher.find()) {
                lcs.add(getSequence(matcher.group(1)));
            }
            keys.put(c, lcs);
            line = r.readLine();
        }
        r.close();
        if (keys.get(' ') == null) {
            List<CharSequence[]> lcs = new ArrayList<CharSequence[]>();
            CharSequence[] cs = new CharSequence[] { KeyEvent.VK_SPACE + "" };
            lcs.add(cs);
            keys.put(' ', lcs);
        }
        if (keys.get('\n') == null) {
            List<CharSequence[]> lcs = new ArrayList<CharSequence[]>();
            CharSequence[] cs = new CharSequence[] { KeyEvent.VK_ENTER + "" };
            lcs.add(cs);
            keys.put('\n', lcs);
        }
    }

    private static CharSequence[] getSequence(String keys) {
        List<CharSequence> lcs = new ArrayList<CharSequence>();
        Scanner scanner = new Scanner(keys);
        while (scanner.hasNext()) {
            String key = scanner.next();
            if (key.equals("SHIFT")) {
                lcs.add(JavaAgentKeys.SHIFT);
            } else if (key.equals("ALT")) {
                lcs.add(JavaAgentKeys.ALT);
            } else if (key.equals("META")) {
                lcs.add(JavaAgentKeys.META);
            } else if (key.equals("CONTROL")) {
                lcs.add(JavaAgentKeys.CONTROL);
            } else {
                String vkCode = "VK_" + key;
                try {
                    lcs.add(KeyEvent.class.getDeclaredField(vkCode).get(null) + "");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
        return lcs.toArray(new CharSequence[lcs.size()]);
    }

    public List<CharSequence[]> getKeys() {
        return keys.get(c);
    }
}
