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
package net.sourceforge.marathon.runtime.api;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.KeyStroke;

import junit.framework.AssertionFailedError;
import net.sourceforge.marathon.api.INamingStrategy;
import net.sourceforge.marathon.api.TestAttributes;
import net.sourceforge.marathon.json.JSONArray;
import net.sourceforge.marathon.json.JSONObject;
import net.sourceforge.marathon.objectmap.ObjectMapException;
import net.sourceforge.marathon.runtime.NamingStrategyFactory;

public class Marathon {

    public static final Logger LOGGER = Logger.getLogger(Marathon.class.getName());

    public interface ICloseHandler extends Runnable {

        @Override
        public abstract void run();

        public abstract void setRunNeeded();

    }

    private final class WindowCloseHandler implements ICloseHandler {
        private final String title;
        private boolean runNeeded;

        private WindowCloseHandler(String title) {
            this.title = title;
            this.runNeeded = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sourceforge.marathon.player.ICloseHandler#run()
         */
        @Override
        public void run() {
            if (!runNeeded) {
                return;
            }
            runNeeded = false;
            switchToWindow(title);
            IPropertyAccessor driver = getDriverAsAccessor();
            namingStrategy.setTopLevelComponent(driver);
        }

        /*
         * (non-Javadoc)
         * 
         * @see net.sourceforge.marathon.player.ICloseHandler#setRunNeeded()
         */
        @Override
        public void setRunNeeded() {
            this.runNeeded = true;
        }

        @Override
        public String toString() {
            return "Window: " + title + "(" + runNeeded + ")";
        }
    }

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(Marathon.class.getName());
    @SuppressWarnings("unused")
    private static final ILogger runtimeLogger = RuntimeLogger.getRuntimeLogger();

    protected INamingStrategy namingStrategy;
    private HashMap<Integer, Keys> keyMapping;
    public PlaybackResult result = null;
    private List<UsedAssertion> assertions = new ArrayList<>();

    protected List<ICloseHandler> closeHandlers = new ArrayList<ICloseHandler>();

    public Marathon(String driverURL) {
        namingStrategy = NamingStrategyFactory.get();
        initKeyMap();
    }

    public int getDelayInMS() {
        try {
            return Integer.parseInt(System.getProperty(Constants.PROP_RUNTIME_DELAY, "0"));
        } catch (Exception e) {
            return 0;
        }
    }

    public void window(final String title, long timeout) {
        ICloseHandler r = new WindowCloseHandler(title);
        r.run();
        closeHandlers.add(r);
    }

    public void close() {
        closeHandlers.remove(closeHandlers.size() - 1);
        if (closeHandlers.size() > 0) {
            closeHandlers.get(closeHandlers.size() - 1).setRunNeeded();
        }
    }

    public String getCharSequence(String keys) {
        StringBuilder sb = new StringBuilder();
        if (keys.length() == 1) {
            sb.append(keys);
        } else {
            KeyStrokeParser ksp = new KeyStrokeParser(keys);
            KeyStroke ks = ksp.getKeyStroke();
            CharSequence keys2 = keyMapping.get(ks.getKeyCode());
            if (keys2 == null) {
                String keysText = KeyEvent.getKeyText(ks.getKeyCode());
                if (keysText.length() == 1 && Character.isUpperCase(keysText.charAt(0))) {
                    keysText = keysText.toLowerCase();
                }
                keys2 = keysText;
            }
            int modifiers = ks.getModifiers();
            if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
                sb.append(Keys.SHIFT);
            }
            if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
                sb.append(Keys.CONTROL);
            }
            if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
                sb.append(Keys.META);
            }
            if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
                sb.append(Keys.ALT);
            }
            sb.append(keys2);
        }
        return sb.toString();
    }

    public void sleepForSlowPlay() {
        if (Boolean.getBoolean("marathon.demo.pause")) {
            while (!Boolean.getBoolean("marathon.demo.resume")) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.setProperty("marathon.demo.pause", "false");
            System.setProperty("marathon.demo.resume", "false");
            return;
        }
        int ms = getDelayInMS();
        if (ms != 0) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
        }
    }

    public void click(Object componentName, boolean isPopupTrigger, Object o1, Object o2, Object o3, Object o4, Object o5) {
        sleepForSlowPlay();
        ArrayList<Object> params = new ArrayList<Object>();
        if (o1 != null) {
            params.add(o1);
        }
        if (o2 != null) {
            params.add(o2);
        }
        if (o3 != null) {
            params.add(o3);
        }
        if (o4 != null) {
            params.add(o4);
        }
        if (o5 != null) {
            params.add(o5);
        }
        int clickCount = getClickCount(params);
        Point position = getPosition(params);
        String modifiers = getModifiers(params);
        Object componentInfo = getComponentInfo(params);
        ComponentId id = new ComponentId(componentName, componentInfo);
        clickInternal(id, position, clickCount, modifiers, isPopupTrigger);
    }

    private Point getPosition(ArrayList<Object> params) {
        if (params.size() < 2 || !(params.get(0) instanceof Number) || !(params.get(1) instanceof Number)) {
            return null;
        }
        int x = ((Number) params.remove(0)).intValue();
        int y = ((Number) params.remove(0)).intValue();
        return new Point(x, y);
    }

    private int getClickCount(ArrayList<Object> params) {
        if (params.size() < 1 || !(params.get(0) instanceof Number)) {
            return 1;
        }
        if (params.size() == 1 || params.size() > 1 && !(params.get(1) instanceof Number)
                || params.size() > 2 && params.get(1) instanceof Number && params.get(2) instanceof Number) {
            return ((Number) params.remove(0)).intValue();
        }
        return 1;
    }

    private String getModifiers(ArrayList<Object> params) {
        if (params.size() < 1 || !(params.get(0) instanceof String)) {
            return null;
        }
        if (params.size() > 1) {
            return (String) params.remove(0);
        }
        try {
            new KeyStrokeParser((String) params.get(0) + "+A");
            return (String) params.remove(0);
        } catch (Exception e) {
            return null;
        }
    }

    private Object getComponentInfo(ArrayList<Object> params) {
        if (params.size() < 1) {
            return null;
        }
        return params.remove(0);
    }

    public void initKeyMap() {
        keyMapping = new HashMap<Integer, Keys>();
        Field[] fields = KeyEvent.class.getDeclaredFields();
        List<Field> asList = Arrays.asList(fields);
        Keys[] values = Keys.values();
        for (Keys keys : values) {
            int javaKey = findKey(asList, keys);
            if (javaKey != -1) {
                keyMapping.put(javaKey, keys);
            }
        }
        keyMapping.put(KeyEvent.VK_ENTER, Keys.RETURN);
        keyMapping.put(KeyEvent.VK_LEFT, Keys.ARROW_LEFT);
        keyMapping.put(KeyEvent.VK_RIGHT, Keys.ARROW_RIGHT);
        keyMapping.put(KeyEvent.VK_UP, Keys.ARROW_UP);
        keyMapping.put(KeyEvent.VK_DOWN, Keys.ARROW_DOWN);
        keyMapping.put(KeyEvent.VK_META, Keys.COMMAND);
    }

    private int findKey(List<Field> asList, Keys keys) {
        for (Field field : asList) {
            if (field.getName().equals("VK_" + keys.name())) {
                try {
                    return field.getInt(null);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public IPropertyAccessor getDriverAsAccessor() {
        return new JSONObjectPropertyAccessor(new JSONObject(getWindowDetails()));
    }

    public String[] getCSS(ComponentId componentId, boolean visibility) throws ObjectMapException {
        closeHandlers.get(closeHandlers.size() - 1).run();
        return namingStrategy.toCSS(componentId, visibility);
    }

    public String[] getComponentNames() throws ObjectMapException {
        closeHandlers.get(closeHandlers.size() - 1).run();
        return namingStrategy.getComponentNames();
    }

    public void select(ComponentId id, String text) {
        sleepForSlowPlay();
        selectString(id, text);
    }

    public void select(ComponentId id, List<Map<Object, Object>> v) {
        sleepForSlowPlay();
        String text = convertToJSON(v);
        selectProperties(id, text);
    }

    private String convertToJSON(List<Map<Object, Object>> v) {
        JSONArray ja = new JSONArray();
        for (Map<Object, Object> map : v) {
            Properties p = new Properties();
            Set<Object> keySet = map.keySet();
            for (Object object : keySet) {
                p.setProperty(object.toString(), map.get(object).toString());
            }
            ja.put(new JSONObject(p));
        }
        return ja.toString();
    }

    public DataReader getDataReader(String fileName, IScript script) throws IOException {
        return new DataReader(fileName, script);
    }

    public void selectMenu(String selection, Object keyStroke) {
        sleepForSlowPlay();
        String[] items = selection.split("\\>\\>");
        selectString(new ComponentId(items[0]), selection);
    }

    public void assertEquals(String message, Object expected, Object actual, SourceLine[] bt) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        if (message == null) {
            message = "Assertion failed. Expected = " + expected + " Actual = " + actual;
        }
        AssertionFailedError error = new AssertionFailedError(message);
        result.addFailure(message, bt, error);
        createErrorScreenShotEntry(error, saveScreenShotOnError());
    }

    public void clearAssertions() {
        assertions = new ArrayList<>();
    }

    public void assertEqualsX(ComponentId id, String property, Object expected, Object actual, SourceLine[] bt, boolean equal) {
        assertions.add(new UsedAssertion(id, property, expected == null ? "null" : expected.toString(),
                actual == null ? "null" : actual.toString(), equal));
        if (equal)
            return;
        String message = "Assertion failed for property: " + property + "on component = " + id.toString() + "\n     expected = `"
                + expected + "'\n     actual = `" + actual + "'";
        AssertionFailedError error = new AssertionFailedError(message);
        result.addFailure(message, bt, error);
        createErrorScreenShotEntry(error, saveScreenShotOnError());
    }

    public void assertContains(String message, String expected, String actual, SourceLine[] bt) {
        if (expected == null && actual == null) {
            return;
        }
        if (actual != null && expected != null && actual.indexOf(expected) > 0) {
            return;
        }
        if (message == null) {
            message = "Assertion failed. Expected = " + expected + " Actual = " + actual;
        }
        AssertionFailedError error = new AssertionFailedError(message);
        result.addFailure(message, bt, error);
        createErrorScreenShotEntry(error, saveScreenShotOnError());
    }

    private void createErrorScreenShotEntry(AssertionFailedError error, String fileName) {
        if (fileName == null)
            return;
        IPlaybackListener listener = (IPlaybackListener) TestAttributes.get("listener");
        listener.addErrorScreenShotEntry(error, fileName);
    }

    public void failTest(String message, SourceLine[] bt) {
        RuntimeException e = new RuntimeException(message);
        result.addFailure(message, bt, e);
    }

    public void errorTest(String message, SourceLine[] bt) {
        throw new RuntimeException(message);
    }

    public void assertContentJava(String[][] expected, String actual) {
        JSONArray o = new JSONArray(actual);
        if (expected.length != o.length()) {
            throw new AssertionFailedError(
                    "Invalid Length " + Integer.valueOf(expected.length) + " : " + Integer.valueOf(o.length()));
        }
        for (int i = 0; i < o.length(); i++) {
            if (expected[i].length != o.getJSONArray(i).length()) {
                throw new AssertionFailedError("Invalid Length at index " + i + ", " + Integer.valueOf(expected[i].length) + " : "
                        + Integer.valueOf(o.getJSONArray(i).length()));
            }
            for (int j = 0; j < expected[i].length; j++) {
                if (expected[i][j] != null && !expected[i][j].equals(o.getJSONArray(i).get(j))) {
                    throw new AssertionFailedError(
                            "Data Mismatch at (" + i + "," + j + ") " + expected[i][j] + " : " + o.getJSONArray(i).get(j));
                }
            }
        }
    }

    public File getScreenCapture() {
        try {
            String imgDir = System.getProperty(Constants.PROP_REPORT_DIR);
            if (imgDir != null) {
                File tempFile = File.createTempFile("screencap", ".png", new File(imgDir));
                if (saveScreenShot(tempFile.getAbsolutePath())) {
                    return tempFile;
                }
                tempFile.delete();
                return null;
            } else {
                System.err.println("getScreenCapture(): Image directory is not set");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getErrorScreenShotFile() {
        String captureDir = System.getProperty(Constants.PROP_IMAGE_CAPTURE_DIR);
        if (captureDir != null) {
            File dir = new File(captureDir);
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.matches(".*error[0-9]*.png");
                }
            });
            if (files == null) {
                files = new File[0];
            }
            String prefix = (String) TestAttributes.get("marathon.capture.prefix");
            String errorFile = captureDir + File.separator + prefix + "-error" + Integer.toString(files.length + 1) + ".png";
            return errorFile;
        }
        return null;
    }

    public void cleanUp() {
        // Called by ruby script once the quit() is called
        if (Boolean.getBoolean(Constants.PROP_PLAY_MODE_MARK)) {
            namingStrategy.setDirty();
            namingStrategy.save();
        }
    }

    public void drag(Object componentName, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        ArrayList<Object> params = new ArrayList<Object>();
        if (o1 != null) {
            params.add(o1);
        }
        if (o2 != null) {
            params.add(o2);
        }
        if (o3 != null) {
            params.add(o3);
        }
        if (o4 != null) {
            params.add(o4);
        }
        if (o5 != null) {
            params.add(o5);
        }
        Point start = getPosition(params);
        Point end = getPosition(params);
        String modifiers = getModifiers(params);
        Object componentInfo = getComponentInfo(params);
        dragInternal(new ComponentId(componentName, componentInfo), modifiers, start, end);
    }

    /**
     * Checks if the two files have identical binary content
     * 
     * @return true if identical
     */
    public boolean filesEqual(String path1, String path2) throws Exception {
        File f1 = new File(path1);
        File f2 = new File(path2);
        if (!f1.exists() || !f2.exists()) {
            throw new Exception("File(s) do not exist");
        }
        if (f1.getCanonicalPath().equals(f2.getCanonicalPath())) {
            throw new Exception("Cannot compare the same file with itself");
        }

        long len = f1.length();
        if (len != f2.length()) {
            return false;
        }
        if (len == 0) {
            return true;
        }

        BufferedInputStream bin1 = null;
        BufferedInputStream bin2 = null;

        try {

            bin1 = new BufferedInputStream(new FileInputStream(f1));
            bin2 = new BufferedInputStream(new FileInputStream(f2));

            while (true) {
                int b1 = bin1.read();
                int b2 = bin2.read();
                if (b1 != b2) {
                    bin1.close();
                    bin2.close();
                    return false;
                }
                if (b1 < 0) {
                    bin1.close();
                    bin2.close();
                    return true; // end reached
                }
            }

        } finally {
            try {
                bin1.close();
            } catch (Exception e) {/* ignore */
            }
            try {
                bin2.close();
            } catch (Exception e) {/* ignore */
            }
        }
    }

    public void notSupported(String message) {
        errorTest(message, null);
    }

    public boolean compareImages(String path1, String path2, double differencesInPercent) throws IOException {
        return ImageCompareAction.compare(path1, path2, differencesInPercent);
    }

    // To be Overridden by the script runtime

    public void dragInternal(ComponentId componentId, String modifiers, Point start, Point end) {
    }

    public void quit() {
    }

    public void switchToWindow(String title) {
    }

    public void switchToContext(String title) {
    }

    public String getWindowDetails() {
        return null;
    }

    public void clickInternal(ComponentId id, Point position, int clickCount, String modifiers, boolean isPopupTrigger) {
    }

    public void selectString(ComponentId id, String text) {
    }

    public void selectProperties(ComponentId id, String text) {
    }

    public boolean saveScreenShot(String path) {
        return false;
    }

    public String saveScreenShotOnError() {
        return null;
    }

    public boolean windowMatchingTitle(String title) {
        IPropertyAccessor propertyAccessor = getDriverAsAccessor();
        List<List<String>> namingProperties = namingStrategy.getContainerNamingProperties("window");
        for (List<String> nlist : namingProperties) {
            String currentTitle = createName(nlist, propertyAccessor);
            if (currentTitle == null) {
                continue;
            }
            if (!title.startsWith("/") || title.startsWith("//")) {
                if (title.startsWith("//")) {
                    title = title.substring(2);
                }
                if (title.equals(currentTitle)) {
                    return true;
                }
            } else {
                Pattern pattern = Pattern.compile(title.substring(1));
                if (pattern.matcher(currentTitle).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    private String createName(List<String> nlist, IPropertyAccessor propertyAccessor) {
        StringBuilder sb = new StringBuilder();
        for (String key : nlist) {
            String value = propertyAccessor.getProperty(key);
            if (value == null) {
                return null;
            }
            sb.append(value).append(":");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public String getWinDetails(String title) {
        return new JSONObject().put("title", title)
                .put("containerNP", new JSONObject(namingStrategy.getContainerNamingProperties()))
                .put("allProperties", namingStrategy.getAllProperties()).toString();
    }

    public void saveScreenShotToReport(String title) {
        File file = getScreenCapture();
        if (file != null) {
            IPlaybackListener listener = (IPlaybackListener) TestAttributes.get("listener");
            listener.addScreenShotEntry(title, file.getAbsolutePath(), assertions);
        }
    }
}
