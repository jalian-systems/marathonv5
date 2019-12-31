package net.sourceforge.marathon.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

public class EnvironmentVariables {

    public static final Logger LOGGER = Logger.getLogger(EnvironmentVariables.class.getName());

    // Internet Explorer Driver Properties
    public static final String IE_DRIVER_EXE_PROPERTY = "webdriver.ie.driver";
    public static final String IE_DRIVER_LOGFILE_PROPERTY = "webdriver.ie.driver.logfile";
    public static final String IE_DRIVER_LOGLEVEL_PROPERTY = "webdriver.ie.driver.loglevel";
    public static final String IE_DRIVER_ENGINE_PROPERTY = "webdriver.ie.driver.engine";
    public final static String IE_DRIVER_HOST_PROPERTY = "webdriver.ie.driver.host";
    public final static String IE_DRIVER_EXTRACT_PATH_PROPERTY = "webdriver.ie.driver.extractpath";
    public final static String IE_DRIVER_SILENT_PROPERTY = "webdriver.ie.driver.silent";

    // Chrome Driver Properties
    public static final String CHROME_DRIVER_EXE_PROPERTY = "webdriver.chrome.driver";
    public final static String CHROME_DRIVER_LOG_PROPERTY = "webdriver.chrome.logfile";
    public static final String CHROME_DRIVER_VERBOSE_LOG_PROPERTY = "webdriver.chrome.verboseLogging";
    public static final String CHROME_DRIVER_SILENT_OUTPUT_PROPERTY = "webdriver.chrome.silentOutput";
    public final static String CHROME_DRIVER_WHITELISTED_IPS_PROPERTY = "webdriver.chrome.whitelistedIps";

    // Edge Driver Properties
    public static final String EDGE_DRIVER_EXE_PROPERTY = "webdriver.edge.driver";
    public static final String EDGE_DRIVER_LOG_PROPERTY = "webdriver.edge.logfile";
    public static final String EDGE_DRIVER_VERBOSE_LOG_PROPERTY = "webdriver.edge.verboseLogging";

    // Opera Driver Properties
    public static final String OPERA_DRIVER_EXE_PROPERTY = "webdriver.opera.driver";
    public final static String OPERA_DRIVER_LOG_PROPERTY = "webdriver.opera.logfile";
    public static final String OPERA_DRIVER_VERBOSE_LOG_PROPERTY = "webdriver.opera.verboseLogging";
    public static final String OPERA_DRIVER_SILENT_OUTPUT_PROPERTY = "webdriver.opera.silentOutput";

    // Gecko (Firefox) Driver Properties
    public static final String GECKO_DRIVER_EXE_PROPERTY = "webdriver.gecko.driver";

    public static void setProperties() {
        Field[] declaredFields = EnvironmentVariables.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if ((field.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
                Object object;
                try {
                    object = field.get(null);
                    if (object instanceof String) {
                        String val = System.getenv((String) object);
                        if (val != null)
                            System.setProperty((String) object, val);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
