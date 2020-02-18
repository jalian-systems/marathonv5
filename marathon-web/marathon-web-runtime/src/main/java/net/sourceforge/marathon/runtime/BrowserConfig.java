/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon.runtime;

import java.util.logging.Logger;

import net.sourceforge.marathon.runtime.api.AbstractPreferences;

public class BrowserConfig extends AbstractPreferences {

    public static final Logger LOGGER = Logger.getLogger(BrowserConfig.class.getName());

    public static final String BROWSER_CONFIG_FILE = "browsers.json";

    private static BrowserConfig _instance;

    private BrowserConfig() {
        super(BROWSER_CONFIG_FILE);
    }

    public static BrowserConfig instance() {
        if (_instance == null || _instance.mpd == null)
            _instance = new BrowserConfig();
        return _instance;
    }

}
