package net.sourceforge.marathon.runtime;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Browser {
    private String browserName;
    private String proxy;
    private String[] otherNames;

    private static ObservableList<Browser> browsers = null;
    private boolean canPlay;
    private boolean canRecord;

    public Browser(String browserName, String proxy, String otherNames, boolean canRecord, boolean canPlay) {
        this.browserName = browserName;
        this.proxy = proxy;
        this.canRecord = canRecord;
        this.canPlay = canPlay;
        this.otherNames = otherNames.split(",");
    }

    public static ObservableList<Browser> getBrowsers() {
        if (browsers != null)
            return browsers;
        browsers = FXCollections.observableArrayList();
        JSONArray browserList = new JSONArray(new JSONTokener(Browser.class.getResourceAsStream("/browsers.json")));
        for (int i = 0; i < browserList.length(); i++) {
            JSONObject browser = browserList.getJSONObject(i);
            browsers.add(new Browser(browser.getString("name"), browser.getString("proxy"), browser.getString("abbrev"),
                    browser.getBoolean("canRecord"), browser.getBoolean("canPlay")));
        }
        return browsers;
    }

    @Override
    public String toString() {
        return browserName;
    }

    public static Browser find(String proxy) {
        ObservableList<Browser> list = getBrowsers();
        for (Browser browser : list) {
            if (browser.proxy.equals(proxy))
                return browser;
        }
        return null;
    }

    public String getProxy() {
        return proxy;
    }

    public String getBrowserName() {
        return browserName;
    }

    public String[] getOtherNames() {
        return otherNames;
    }

    public static String findBrowserProxyByName(String name) {
        ObservableList<Browser> browsers = getBrowsers();
        for (Browser browser : browsers) {
            if (name.equalsIgnoreCase(browser.getBrowserName()))
                return browser.getProxy();
            for (String otherName : browser.getOtherNames()) {
                if (name.equalsIgnoreCase(otherName))
                    return browser.getProxy();
            }
        }
        return null;
    }

    public boolean canPlay() {
        return canPlay;
    }

    public boolean canRecord() {
        return canRecord;
    }

}