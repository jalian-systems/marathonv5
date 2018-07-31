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
package net.sourceforge.marathon.fx.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Window;

public class FXUIUtils {

    public static final Logger LOGGER = Logger.getLogger(FXUIUtils.class.getName());

    private static class FontInfo {
        private Font font;
        private INamedCharacter namedChar;
        private Color color;

        public FontInfo(Font glyphFont, INamedCharacter namedChar, Color color) {
            this.font = glyphFont;
            this.namedChar = namedChar;
            this.color = color;
        }

        public FontInfo(Font font, INamedCharacter namedChar) {
            this(font, namedChar, null);
        }

        public FontInfo(INamedCharacter namedChar) {
            this(null, namedChar);
        }

        public Node create() {
            if (font == null) {
                return new ImageView();
            }
            Label label = new Label();
            label.setFont(font);
            if (color != null)
                label.setTextFill(color);
            label.setText(namedChar.getChar() + "");
            return label;
        }

        public Text createText() {
            Text t = new Text(namedChar.getChar() + "");
            t.setFont(font);
            return t;
        }
    }

    public final static Font fontAwesome;
    public final static Font materialIcons;
    public final static Font materialDesignIcons;
    public final static Font icoMoon;
    public static INamedCharacter SPACE = new INamedCharacter() {

        @Override
        public String name() {
            return "space";
        }

        @Override
        public char getChar() {
            return ' ';
        }
    };
    public final static Map<String, FontInfo> fontIcons = new HashMap<>();
    static {
        fontAwesome = Font.loadFont(FontAwesome.getFontFile(), 16);
        materialIcons = Font.loadFont(MaterialIcons.getFontFile(), 16);
        icoMoon = Font.loadFont(IcoMoon.getFontFile(), 16);
        materialDesignIcons = Font.loadFont(MaterialDesignIcons.getFontFile(), 16);
        fontIcons.put("ok", new FontInfo(fontAwesome, FontAwesome.ICON.CHECK, Color.DARKGREEN));
        fontIcons.put("tag", new FontInfo(fontAwesome, FontAwesome.ICON.HASHTAG));
        fontIcons.put("edit", new FontInfo(fontAwesome, FontAwesome.ICON.EDIT));
        fontIcons.put("new", new FontInfo(fontAwesome, FontAwesome.ICON.PLUS));
        fontIcons.put("cancel", new FontInfo(fontAwesome, FontAwesome.ICON.REMOVE, Color.ORANGERED));
        fontIcons.put("browse", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FILE_TREE));
        fontIcons.put("newTestcase", new FontInfo(fontAwesome, FontAwesome.ICON.PLUS));
        fontIcons.put("save", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CONTENT_SAVE, Color.STEELBLUE));
        fontIcons.put("saveAs", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CONTENT_SAVE_SETTINGS, Color.STEELBLUE));
        fontIcons.put("saveAll", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CONTENT_SAVE_ALL, Color.STEELBLUE));
        fontIcons.put("record", new FontInfo(fontAwesome, FontAwesome.ICON.CIRCLE, Color.RED));
        fontIcons.put("pause", new FontInfo(fontAwesome, FontAwesome.ICON.PAUSE));
        fontIcons.put("resumeRecording", new FontInfo(materialIcons, MaterialIcons.ICON.SKIP_NEXT));
        fontIcons.put("insertScript", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FUNCTION));
        fontIcons.put("insertChecklist", new FontInfo(materialIcons, MaterialIcons.ICON.PLAYLIST_ADD_CHECK));
        fontIcons.put("stop", new FontInfo(fontAwesome, FontAwesome.ICON.STOP, Color.RED));
        fontIcons.put("stopPlay", new FontInfo(fontAwesome, FontAwesome.ICON.STOP, Color.RED));
        fontIcons.put("recorderConsole", new FontInfo(fontAwesome, FontAwesome.ICON.TERMINAL));
        fontIcons.put("openApplication", new FontInfo(materialIcons, MaterialIcons.ICON.OPEN_IN_NEW));
        fontIcons.put("closeApplication", new FontInfo(fontAwesome, FontAwesome.ICON.REMOVE, Color.RED));
        fontIcons.put("play", new FontInfo(fontAwesome, FontAwesome.ICON.PLAY, Color.GREEN));
        fontIcons.put("slowPlay", new FontInfo(materialIcons, MaterialIcons.ICON.SLOW_MOTION_VIDEO, Color.GREEN));
        fontIcons.put("debug", new FontInfo(fontAwesome, FontAwesome.ICON.BUG));
        fontIcons.put("toggleBreakpoint", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.TOGGLE_SWITCH, Color.GREEN));
        fontIcons.put("resumePlaying", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.PLAY_PAUSE));
        fontIcons.put("stepInto", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_INTO));
        fontIcons.put("stepOver", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_OVER));
        fontIcons.put("stepReturn", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_OUT));
        fontIcons.put("playerConsole", new FontInfo(fontAwesome, FontAwesome.ICON.TERMINAL));
        fontIcons.put("showReport", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FILE_CHART));
        fontIcons.put("extractModule", new FontInfo(fontAwesome, FontAwesome.ICON.EXTERNAL_LINK));
        fontIcons.put("createDDT", new FontInfo(icoMoon, IcoMoon.ICON.TABLE2));
        fontIcons.put("createDataLoop", new FontInfo(icoMoon, IcoMoon.ICON.LOOP));
        fontIcons.put("objectMapCreate", new FontInfo(fontAwesome, FontAwesome.ICON.MAP_MARKER));
        fontIcons.put("editObjectMap", new FontInfo(fontAwesome, FontAwesome.ICON.MAP_O));
        fontIcons.put("editObjectMapConfiguration", new FontInfo(fontAwesome, FontAwesome.ICON.SITEMAP));
        fontIcons.put("cut", new FontInfo(fontAwesome, FontAwesome.ICON.CUT));
        fontIcons.put("copy", new FontInfo(fontAwesome, FontAwesome.ICON.COPY));
        fontIcons.put("paste", new FontInfo(fontAwesome, FontAwesome.ICON.PASTE));
        fontIcons.put("undo", new FontInfo(fontAwesome, FontAwesome.ICON.ROTATE_LEFT));
        fontIcons.put("redo", new FontInfo(fontAwesome, FontAwesome.ICON.ROTATE_RIGHT));
        fontIcons.put("search", new FontInfo(fontAwesome, FontAwesome.ICON.SEARCH));
        fontIcons.put("replace", new FontInfo(fontAwesome, FontAwesome.ICON.SEARCH_PLUS));
        fontIcons.put("settings", new FontInfo(fontAwesome, FontAwesome.ICON.GEARS));
        fontIcons.put("fldr_obj", new FontInfo(fontAwesome, FontAwesome.ICON.FOLDER_OPEN, Color.DARKSLATEGREY));
        fontIcons.put("fldr_closed", new FontInfo(fontAwesome, FontAwesome.ICON.FOLDER, Color.DARKSLATEGREY));
        fontIcons.put("prj_obj", new FontInfo(fontAwesome, FontAwesome.ICON.CONNECTDEVELOP, Color.DARKSLATEBLUE));
        fontIcons.put("file", new FontInfo(fontAwesome, FontAwesome.ICON.FILE, Color.DARKSLATEGREY));
        fontIcons.put("file_obj", new FontInfo(fontAwesome, FontAwesome.ICON.FILE, Color.DARKSLATEGREY));
        fontIcons.put("rename", new FontInfo(fontAwesome, FontAwesome.ICON.PENCIL));
        fontIcons.put("expandAll", new FontInfo(fontAwesome, FontAwesome.ICON.EXPAND));
        fontIcons.put("collapseAll", new FontInfo(fontAwesome, FontAwesome.ICON.COMPRESS));
        fontIcons.put("delete", new FontInfo(fontAwesome, FontAwesome.ICON.MINUS));
        fontIcons.put("remove", new FontInfo(fontAwesome, FontAwesome.ICON.MINUS));
        fontIcons.put("refresh", new FontInfo(fontAwesome, FontAwesome.ICON.REFRESH));
        fontIcons.put("add", new FontInfo(fontAwesome, FontAwesome.ICON.PLUS));
        fontIcons.put("add-to-right", new FontInfo(fontAwesome, FontAwesome.ICON.ANGLE_DOUBLE_RIGHT));
        fontIcons.put("up", new FontInfo(fontAwesome, FontAwesome.ICON.ARROW_UP));
        fontIcons.put("down", new FontInfo(fontAwesome, FontAwesome.ICON.ARROW_DOWN));
        fontIcons.put("info", new FontInfo(icoMoon, IcoMoon.ICON.INFO));
        fontIcons.put("warn", new FontInfo(icoMoon, IcoMoon.ICON.WARNING, Color.ORANGE));
        fontIcons.put("error", new FontInfo(icoMoon, IcoMoon.ICON.CANCEL_CIRCLE, Color.RED));
        fontIcons.put("rawRecord", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MATRIX));
        fontIcons.put("console_view", new FontInfo(fontAwesome, FontAwesome.ICON.TERMINAL));
        fontIcons.put("clear", new FontInfo(materialIcons, MaterialIcons.ICON.PHONELINK_ERASE));
        fontIcons.put("export", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FILE_EXPORT));
        fontIcons.put("showreport", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.VIEW_LIST));
        fontIcons.put("show_message", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MESSAGE_REPLY_TEXT));
        fontIcons.put("et", new FontInfo(materialIcons, MaterialIcons.ICON.CENTER_FOCUS_STRONG));
        fontIcons.put("newModule", new FontInfo(SPACE));
        fontIcons.put("newFixture", new FontInfo(SPACE));
        fontIcons.put("newModuleDir", new FontInfo(SPACE));
        fontIcons.put("newCheckList", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CHECKBOX_MARKED_OUTLINE));
        fontIcons.put("exit", new FontInfo(fontAwesome, FontAwesome.ICON.SIGN_OUT));
        fontIcons.put("preferences", new FontInfo(materialIcons, MaterialIcons.ICON.SETTINGS));
        fontIcons.put("selectFixture", new FontInfo(materialIcons, MaterialIcons.ICON.GPS_FIXED));
        fontIcons.put("empty", new FontInfo(SPACE));
        fontIcons.put("clearAllBreakpoints", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.TOGGLE_SWITCH_OFF));
        fontIcons.put("projectSettings", new FontInfo(fontAwesome, FontAwesome.ICON.GEARS));
        fontIcons.put("manageChecklists", new FontInfo(SPACE));
        fontIcons.put("Object Map Server...", new FontInfo(SPACE));
        fontIcons.put("omapServer", new FontInfo(fontAwesome, FontAwesome.ICON.MAP_O));
        fontIcons.put("Welcome Message", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.BANK));
        fontIcons.put("Microsoft Edge", new FontInfo(fontAwesome, FontAwesome.ICON.EDGE));
        fontIcons.put("Firefox", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Firefox (Marionette)", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Opera", new FontInfo(fontAwesome, FontAwesome.ICON.OPERA));
        fontIcons.put("Firefox", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Chrome", new FontInfo(fontAwesome, FontAwesome.ICON.CHROME));
        fontIcons.put("Firefox (Record)", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Chrome (Record)", new FontInfo(fontAwesome, FontAwesome.ICON.CHROME));
        fontIcons.put("Internet Explorer", new FontInfo(fontAwesome, FontAwesome.ICON.INTERNET_EXPLORER));
        fontIcons.put("resetWorkspace", new FontInfo(SPACE));
        fontIcons.put("releaseNotes", new FontInfo(SPACE));
        fontIcons.put("changeLog", new FontInfo(SPACE));
        fontIcons.put("visitWebsite", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.WEB));
        fontIcons.put("helpAbout", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.HELP));
        fontIcons.put("newIssueFile", new FontInfo(fontAwesome, FontAwesome.ICON.EXCLAMATION_CIRCLE));
        fontIcons.put("tissue", new FontInfo(fontAwesome, FontAwesome.ICON.EXCLAMATION_CIRCLE));
        fontIcons.put("newStoryFile", new FontInfo(materialIcons, MaterialIcons.ICON.LIBRARY_BOOKS));
        fontIcons.put("tstory", new FontInfo(materialIcons, MaterialIcons.ICON.LIBRARY_BOOKS));
        fontIcons.put("newFeatureFile", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MOVIE));
        fontIcons.put("tfeature", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MOVIE));
        fontIcons.put("newSuiteFile", new FontInfo(materialIcons, MaterialIcons.ICON.VIDEO_LIBRARY));
        fontIcons.put("tsuite", new FontInfo(materialIcons, MaterialIcons.ICON.VIDEO_LIBRARY));
        fontIcons.put("duplicate-row", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.TABLE_ROW_PLUS_AFTER));
        fontIcons.put("header", new FontInfo(fontAwesome, FontAwesome.ICON.HEADER));
        fontIcons.put("checklist", new FontInfo(materialIcons, MaterialIcons.ICON.PLAYLIST_ADD_CHECK));
        fontIcons.put("textbox", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FORMAT_TEXT));
        fontIcons.put("expandall", new FontInfo(fontAwesome, FontAwesome.ICON.EXPAND));
        fontIcons.put("collapseall", new FontInfo(fontAwesome, FontAwesome.ICON.COMPRESS));
        fontIcons.put("clearSearch", new FontInfo(fontAwesome, FontAwesome.ICON.SEARCH_MINUS));
        fontIcons.put("list", new FontInfo(materialIcons, MaterialIcons.ICON.FORMAT_LIST_BULLETED));
        fontIcons.put("open", new FontInfo(SPACE));
        fontIcons.put("params", new FontInfo(materialIcons, MaterialIcons.ICON.FORMAT_LIST_BULLETED));
        fontIcons.put("table", new FontInfo(icoMoon, IcoMoon.ICON.TABLE2));
        fontIcons.put("data_loop", new FontInfo(icoMoon, IcoMoon.ICON.LOOP));
        fontIcons.put("script", new FontInfo(SPACE));
        fontIcons.put("convert_command", new FontInfo(SPACE));
        fontIcons.put("extract_module", new FontInfo(fontAwesome, FontAwesome.ICON.EXTERNAL_LINK));
        fontIcons.put("extract", new FontInfo(SPACE));
        fontIcons.put("Save as Default", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DOWNLOAD));
        fontIcons.put("saveas", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CONTENT_SAVE_SETTINGS));
        fontIcons.put("loaddefaults", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.UPLOAD));
        fontIcons.put("testrunner", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.RUN));
        fontIcons.put("nextFailure", new FontInfo(icoMoon, IcoMoon.ICON.ARROW_DOWN));
        fontIcons.put("prevFailure", new FontInfo(icoMoon, IcoMoon.ICON.ARROW_UP));
        fontIcons.put("report", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.VIEW_LIST));
        fontIcons.put("trace", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.STACKEXCHANGE));
        fontIcons.put("run", new FontInfo(fontAwesome, FontAwesome.ICON.PLAY));
        fontIcons.put("runSelected", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.RUN));
        fontIcons.put("failure", new FontInfo(materialIcons, MaterialIcons.ICON.CLEAR));
        fontIcons.put("failures", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.PLAYLIST_REMOVE));
        fontIcons.put("ttest", new FontInfo(icoMoon, IcoMoon.ICON.FILE_PLAY, Color.GREEN));
        fontIcons.put("test", new FontInfo(icoMoon, IcoMoon.ICON.FILE_PLAY, Color.GREEN));
        fontIcons.put("testrun", new FontInfo(fontAwesome, FontAwesome.ICON.PLAY));
        fontIcons.put("testok", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CHECK));
        fontIcons.put("testerror", new FontInfo(materialIcons, MaterialIcons.ICON.ERROR));
        fontIcons.put("testfail", new FontInfo(materialIcons, MaterialIcons.ICON.CLEAR));
        fontIcons.put("tsuiterun", new FontInfo(fontAwesome, FontAwesome.ICON.PLAY));
        fontIcons.put("tsuiteok", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CHECK_ALL));
        fontIcons.put("tsuiteerror", new FontInfo(fontAwesome, FontAwesome.ICON.EXCLAMATION_TRIANGLE));
        fontIcons.put("tsuitefail", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.PLAYLIST_REMOVE));
        fontIcons.put("favourite", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.STAR));
        fontIcons.put("removeHistory", new FontInfo(fontAwesome, FontAwesome.ICON.REMOVE));
        fontIcons.put("clearSavedHistory", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.STAR_OFF));
        fontIcons.put("clearUnSavedHistory", new FontInfo(materialIcons, MaterialIcons.ICON.REMOVE_SHOPPING_CART));
        fontIcons.put("addfolder", new FontInfo(materialIcons, MaterialIcons.ICON.CREATE_NEW_FOLDER));
        fontIcons.put("open-in-browser", new FontInfo(materialIcons, MaterialIcons.ICON.OPEN_IN_NEW));
        fontIcons.put("insert", new FontInfo(materialIcons, MaterialIcons.ICON.PLAYLIST_ADD_CHECK));
        fontIcons.put("cleanObjectMapFolders", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FOLDER_REMOVE));
        fontIcons.put("csvFile", new FontInfo(fontAwesome, FontAwesome.ICON.FILE_EXCEL_O));
        fontIcons.put("addjar", new FontInfo(icoMoon, IcoMoon.ICON.FILE_ZIP));
        fontIcons.put("credits", new FontInfo(fontAwesome, FontAwesome.ICON.THUMBS_O_UP));
        fontIcons.put("properties", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DOTS_HORIZONTAL));
        fontIcons.put("goto", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CURSOR_DEFAULT_OUTLINE));
        fontIcons.put("screencapture", new FontInfo(fontAwesome, FontAwesome.ICON.PICTURE_O));
        fontIcons.put("next", new FontInfo(icoMoon, IcoMoon.ICON.ARROW_RIGHT));
        fontIcons.put("prev", new FontInfo(icoMoon, IcoMoon.ICON.ARROW_LEFT));
        fontIcons.put("help", new FontInfo(materialIcons, MaterialIcons.ICON.HELP_OUTLINE));
        fontIcons.put("rotate", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.SCREEN_ROTATION));
        fontIcons.put("Safari", new FontInfo(fontAwesome, FontAwesome.ICON.SAFARI));
        fontIcons.put("PhantomJS", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.GHOST));
        fontIcons.put("wordWrap", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.WRAP));
        fontIcons.put("close", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CLOSE_CIRCLE));
    }

    public static Button createButton(String name, String toolTip) {
        return createButton(name, toolTip, true);
    }

    public static Button createButton(String name, String toolTip, boolean enabled) {
        return createButton(name, toolTip, enabled, null);
    }

    enum FromOptions {
        NULL_IF_NOT_EXISTS, EMPTY_IF_NOT_EXISTS
    };

    public static Button createButton(String name, String toolTip, boolean enabled, String buttonText) {
        return (Button) _initButtonBase(name, toolTip, enabled, buttonText, new Button());
    }

    public static ButtonBase _initButtonBase(String name, String toolTip, boolean enabled, String buttonText, ButtonBase button) {
        button.setId(name + "Button");
        button.setTooltip(new Tooltip(toolTip));
        Node enabledIcon = getImageFrom(name, "icons/", FromOptions.NULL_IF_NOT_EXISTS);
        if (enabledIcon != null) {
            button.setText(null);
            button.setGraphic(enabledIcon);
        }
        if (buttonText != null) {
            button.setText(buttonText);
        } else if (enabledIcon == null) {
            button.setText(name);
        }
        button.setDisable(!enabled);
        button.setMinWidth(Region.USE_PREF_SIZE);
        return button;
    }

    public static ToggleButton createToggleButton(String name, String toolTip) {
        return createToggleButton(name, toolTip, true);
    }

    public static ToggleButton createToggleButton(String name, String toolTip, boolean enabled) {
        return createToggleButton(name, toolTip, enabled, null);
    }

    public static ToggleButton createToggleButton(String name, String toolTip, boolean enabled, String buttonText) {
        return (ToggleButton) _initButtonBase(name, toolTip, enabled, buttonText, new ToggleButton());
    }

    public static MenuItem createMenuItem(String name, String commandName, String mnemonic) {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(name + "MenuItem");
        Node enabledIcon = getImageFrom(name, "icons/", FromOptions.NULL_IF_NOT_EXISTS);
        if (enabledIcon != null) {
            menuItem.setGraphic(enabledIcon);
        }
        menuItem.setText(commandName);
        if (!"".equals(mnemonic)) {
            menuItem.setAccelerator(KeyCombination.keyCombination(mnemonic));
        }

        return menuItem;
    }

    public static MenuItem createCheckboxMenuItem(String name, String commandName, String mnemonic) {
        MenuItem menuItem = new CheckMenuItem();
        menuItem.setId(name + "MenuItem");
        Node enabledIcon = getImageFrom(name, "icons/", FromOptions.NULL_IF_NOT_EXISTS);
        if (enabledIcon != null) {
            menuItem.setGraphic(enabledIcon);
        }
        menuItem.setText(commandName);
        if (!"".equals(mnemonic)) {
            menuItem.setAccelerator(KeyCombination.keyCombination(mnemonic));
        }
        return menuItem;
    }

    public static Node getIcon(String name) {
        return getImageFrom(name, "icons/");
    }

    public static Node getImageFrom(String name, String from) {
        return getImageFrom(name, from, FromOptions.EMPTY_IF_NOT_EXISTS);
    }

    public static Node getImageFrom(String name, String from, FromOptions options) {
        Node node = getImageFromX(name, from, FromOptions.NULL_IF_NOT_EXISTS);
        if (node == null) {
            return options == FromOptions.NULL_IF_NOT_EXISTS ? null : new ImageView();
        }
        return node;
    }

    static List<String> noFontIcon = new ArrayList<>();

    public static Text getIconAsText(String name) {
        FontInfo fontInfo = fontIcons.get(name);
        if (fontInfo == null)
            return null;
        return fontInfo.createText();
    }

    public static Node getImageFromX(String name, String from, FromOptions options) {
        FontInfo fontInfo = fontIcons.get(name);
        if (fontInfo != null) {
            return fontInfo.create();
        } else {
            if (noFontIcon.contains(name)) {
                ;
            } else {
                noFontIcon.add(name);
            }
        }
        URL resource = FXUIUtils.class.getResource(from + name + ".gif");
        if (resource == null) {
            resource = FXUIUtils.class.getResource(from + name + ".png");
        }
        if (resource == null) {
            return options == FromOptions.NULL_IF_NOT_EXISTS ? null : new ImageView();
        }
        return new ImageView(resource.toExternalForm());
    }

    public static Node getImage(String name) {
        return getImageFrom(name, "images/");
    }

    public static Image getImageURL(String name) {
        URL resource = FXUIUtils.class.getResource("images/" + name + ".png");
        if (resource == null)
            resource = FXUIUtils.class.getResource("images/" + name + ".gif");
        return new Image(resource.toExternalForm());
    }

    public static Node createFiller() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    public static void showMessageDialog(Window parent, String message, String title, AlertType type) {
        showMessageDialog(parent, message, title, type, false);
    }

    public static void showMessageDialog(Window parent, String message, String title, AlertType type, boolean monospace) {
        if (Platform.isFxApplicationThread()) {
            _showMessageDialog(parent, message, title, type, monospace);
        } else {
            Object lock = new Object();
            synchronized (lock) {
                Platform.runLater(() -> {
                    _showMessageDialog(parent, message, title, type, monospace);
                    lock.notifyAll();
                });
            }
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void _showMessageDialog(Window parent, String message, String title, AlertType type) {
        _showMessageDialog(parent, message, title, type, false);
    }

    public static void _showMessageDialog(Window parent, String message, String title, AlertType type, boolean monospace) {
        Alert alert = new Alert(type);
        alert.initOwner(parent);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setResizable(true);
        if (monospace) {
            Text text = new Text(message);
            alert.getDialogPane().setStyle("-fx-padding: 0 10px 0 10px;");
            text.setStyle(" -fx-font-family: monospace;");
            alert.getDialogPane().contentProperty().set(text);
        }
        alert.showAndWait();
    }

    @SuppressWarnings("unchecked")
    public static Optional<ButtonType> showConfirmDialog(Window parent, String message, String title, AlertType type,
            ButtonType... buttonTypes) {
        if (Platform.isFxApplicationThread()) {
            return _showConfirmDialog(parent, message, title, type, buttonTypes);
        } else {
            Object r[] = { null };
            Object lock = new Object();
            synchronized (lock) {
                Platform.runLater(() -> {
                    r[0] = _showConfirmDialog(parent, message, title, type, buttonTypes);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                });
            }
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return (Optional<ButtonType>) r[0];
        }
    }

    public static ButtonType YES_ALL = new ButtonType("Yes to All", ButtonData.APPLY);

    public static Optional<ButtonType> _showConfirmDialog(Window parent, String message, String title, AlertType type,
            ButtonType... buttonTypes) {
        Alert alert = new Alert(type, message, buttonTypes);
        alert.initOwner(parent);
        alert.setTitle(title);
        alert.initModality(Modality.APPLICATION_MODAL);
        return alert.showAndWait();
    }

    public static void showExceptionMessage(String message, IOException e) {
        showMessageDialog(null, message + ": " + e.getMessage(), "Error", AlertType.ERROR);
    }

    public static File showSaveFileChooser(String title, File initialDirectory, Window ownerWindow, ExtensionFilter filter) {
        return getFileChooser(title, initialDirectory, ownerWindow, filter).showSaveDialog(ownerWindow);
    }

    public static File showOpenFileChooser(String title, File initialDirectory, Window ownerWindow, ExtensionFilter filter) {
        return getFileChooser(title, initialDirectory, ownerWindow, filter).showOpenDialog(ownerWindow);
    }

    public static List<File> showOpenMultipleFileChooser(String title, File initialDirectory, Window ownerWindow,
            ExtensionFilter filter) {
        return getFileChooser(title, initialDirectory, ownerWindow, filter).showOpenMultipleDialog(ownerWindow);
    }

    private static FileChooser getFileChooser(String title, File initialDirectory, Window ownerWindow, ExtensionFilter filter) {
        FileChooser fileChooser = new FileChooser();
        if (filter != null) {
            fileChooser.getExtensionFilters().add(filter);
        }
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(initialDirectory);
        return fileChooser;
    }

    public static File showDirectoryChooser(String title, File initialDirectory, Window ownerWindow) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(initialDirectory);
        return directoryChooser.showDialog(ownerWindow);
    }

}
