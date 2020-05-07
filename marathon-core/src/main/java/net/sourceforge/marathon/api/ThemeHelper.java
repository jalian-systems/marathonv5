package net.sourceforge.marathon.api;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

public class ThemeHelper {

    private Style style = Style.DARK;
    private SimpleBooleanProperty selectedDarkTheme = new SimpleBooleanProperty(true);
    private static ThemeHelper themeHelper;

    private ThemeHelper() {
        selectedDarkTheme.addListener((b, o, n) -> setDarkTheme(n));
        loadThemes();
    }

    protected void setDarkTheme(Boolean newValue) {
        if (newValue) {
            style = Style.DARK;
        } else {
            style = Style.LIGHT;
        }
        selectedDarkTheme.set(newValue);
        saveStyle();
    }

    public void setSceneStyle(Scene scene) {
        if (scene != null) {
            JMetro jMetro = new JMetro(scene, style);
            jMetro.getOverridingStylesheets().add(ThemeHelper.class.getClassLoader()
                    .getResource("net/sourceforge/marathon/fx/api/css/marathon.css").toExternalForm());
        }
    }

    public SimpleBooleanProperty selectedDarkThemePropety() {
        return selectedDarkTheme;
    }

    public boolean isDarkThemeSelected() {
        return selectedDarkTheme.get();
    }

    private void loadThemes() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        boolean darkTheme = p.getBoolean("darkTheme", true);
        setDarkTheme(darkTheme);
    }

    private void saveStyle() {
        Preferences p = Preferences.userNodeForPackage(this.getClass());
        try {
            p.clear();
            p.flush();
            p = Preferences.userNodeForPackage(this.getClass());
            p.putBoolean("darkTheme", selectedDarkTheme.get());
            p.flush();
        } catch (BackingStoreException e) {
            return;
        }
    }

    public static ThemeHelper _getInstance() {
        if (themeHelper == null) {
            themeHelper = new ThemeHelper();
        }
        return themeHelper;
    }

    public class StyleClassHelper {

        public static final String BACKGROUND = JMetroStyleClass.BACKGROUND;
        public static final String LIGHT_BUTTONS = JMetroStyleClass.LIGHT_BUTTONS;
        public static final String UNDERLINE_TAB_PANE = JMetroStyleClass.UNDERLINE_TAB_PANE;
        public static final String ALTERNATING_ROW_COLORS = JMetroStyleClass.ALTERNATING_ROW_COLORS;
    }
}
