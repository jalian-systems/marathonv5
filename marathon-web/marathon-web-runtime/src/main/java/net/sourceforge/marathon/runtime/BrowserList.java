package net.sourceforge.marathon.runtime;

import java.util.StringJoiner;

import com.jaliansystems.marathonite.api.ISupportedBrowserListProvider;

import javafx.collections.ObservableList;

public class BrowserList implements ISupportedBrowserListProvider {

    @Override
    public String getSupportedBrowserList() {
        StringJoiner joiner = new StringJoiner(",");
        ObservableList<Browser> browsers = Browser.getBrowsers();
        for (Browser browser : browsers) {
            // joiner.add(browser.getBrowserName());
            String[] otherNames = browser.getOtherNames();
            for (String name : otherNames) {
                joiner.add(name);
            }
        }
        return joiner.toString();
    }

}
