package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import org.json.JSONObject;

import javafx.scene.control.Alert.AlertType;
import net.sourceforge.marathon.fx.api.FXUIUtils;

public class ProjectFile {

    public static final Logger LOGGER = Logger.getLogger(ProjectFile.class.getName());

    public static void updateProjectProperty(String p, String v) throws FileNotFoundException, IOException {
        Properties props = getProjectProperties();
        props.setProperty(p, v);
        updateProperties(props);
    }

    public static String getProjectProperty(String p) throws FileNotFoundException, IOException {
        Properties props = getProjectProperties();
        return props.getProperty(p, "");
    }

    public static boolean isValidProjectDirectory(File file) {
        return file.exists() && file.isDirectory()
                && (new File(file, ProjectFile.PROJECT_FILE).exists() || new File(file, ".project").exists());
    }

    public static Properties getProjectProperties() throws FileNotFoundException, IOException {
        return getProjectProperties(Preferences.instance(), null);
    }

    public static Properties getProjectProperties(Preferences instance, String dirName) throws FileNotFoundException, IOException {
        JSONObject settings = instance.getSection("project-settings");
        String[] names = JSONObject.getNames(settings);
        if (names != null && names.length > 0) {
            return toProperties(settings);
        }
        FileInputStream input = null;
        try {
            dirName = dirName == null ? System.getProperty(Constants.PROP_PROJECT_DIR) : dirName;
            input = new FileInputStream(new File(dirName, ".project"));
            Properties properties = new Properties();
            properties.load(input);
            instance.saveSection("project-settings", toJSON(properties));
            FXUIUtils.showMessageDialog(null,
                    "Project settings for `" + dirName + "` are saved into project.json.\n" + "You can delete the .project file.",
                    "Saved Settings", AlertType.INFORMATION);
            return properties;
        } finally {
            input.close();
        }
    }

    public static Properties getProjectProperties(String dirName) throws FileNotFoundException, IOException {
        return getProjectProperties(new Preferences(new File(dirName)), dirName);
    }

    private static JSONObject toJSON(Properties properties) {
        JSONObject o = new JSONObject();
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            o.put(key, properties.get(key));
        }
        return o;
    }

    private static Properties toProperties(JSONObject settings) {
        Properties p = new Properties();
        String[] names = JSONObject.getNames(settings);
        for (String name : names) {
            p.setProperty(name, settings.getString(name));
        }
        return p;
    }

    public static void updateProperties(Properties properties) throws FileNotFoundException, IOException {
        ProjectFile.updateProperties(Preferences.instance(), properties);
    }

    public static void updateProperties(File projectDir, Properties properties) throws IOException, FileNotFoundException {
        updateProperties(new Preferences(projectDir), properties);
    }

    public static void updateProperties(Preferences instance, Properties properties) {
        JSONObject section = instance.getSection("project-settings");
        Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            section.put(key, properties.getProperty(key));
        }
        instance.saveSection("project-settings", section);
    }

    public static final String PROJECT_FILE = System.getProperty("marathon.project.file", "project.json");

}
