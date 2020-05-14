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
import net.sourceforge.marathon.display.MarathonFileChooser;
import net.sourceforge.marathon.display.MarathonFileChooserInfo;

public class FXUIUtils {

    public static final Logger LOGGER = Logger.getLogger(FXUIUtils.class.getName());

    private static class FontInfo {
        private Font font;
        private INamedCharacter namedChar;
        private Color color;
        private String fontSuffix;

        public FontInfo(Font glyphFont, INamedCharacter namedChar, Color color) {
            this.font = glyphFont;
            this.namedChar = namedChar;
            this.color = color;
            if (glyphFont == fontAwesome) {
                fontSuffix = "fa";
            } else if (glyphFont == materialIcons) {
                fontSuffix = "mi";
            } else if (glyphFont == materialDesignIcons) {
                fontSuffix = "mdi";
            } else if (glyphFont == icoMoon) {
                fontSuffix = "im";
            } else if (glyphFont == mdl2Icons) {
                fontSuffix = "mdl2";
            }
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
            label.getStyleClass().add("image-label-" + fontSuffix);
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
    public final static Font mdl2Icons;
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
        mdl2Icons = Font.loadFont(MDL2Icons.getFontFile(), 16);
        fontIcons.put("ok", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHECK_MARK));
        fontIcons.put("tag", new FontInfo(fontAwesome, FontAwesome.ICON.HASHTAG));
        fontIcons.put("edit", new FontInfo(mdl2Icons, MDL2Icons.ICON.EDIT));
        fontIcons.put("new", new FontInfo(mdl2Icons, MDL2Icons.ICON.SUBSCRIPTION_ADD));
        fontIcons.put("cancel", new FontInfo(mdl2Icons, MDL2Icons.ICON.CANCEL, Color.ORANGERED));
        fontIcons.put("browse", new FontInfo(mdl2Icons, MDL2Icons.ICON.TREE_FOLDER_FOLDER_OPEN));
        fontIcons.put("newTestcase", new FontInfo(mdl2Icons, MDL2Icons.ICON.NEW_FOLDER));
        fontIcons.put("save", new FontInfo(mdl2Icons, MDL2Icons.ICON.SAVE, Color.STEELBLUE));
        fontIcons.put("saveAs", new FontInfo(mdl2Icons, MDL2Icons.ICON.SAVE_AS, Color.STEELBLUE));
        fontIcons.put("saveAll", new FontInfo(mdl2Icons, MDL2Icons.ICON.SAVE_COPY, Color.STEELBLUE));
        fontIcons.put("record", new FontInfo(mdl2Icons, MDL2Icons.ICON.VIDEO_SOLID, Color.RED));
        fontIcons.put("pause", new FontInfo(mdl2Icons, MDL2Icons.ICON.PAUSE_BADGE12));
        fontIcons.put("resumeRecording", new FontInfo(mdl2Icons, MDL2Icons.ICON.RETURN_TO_CALL));
        fontIcons.put("insertScript", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.FUNCTION));
        fontIcons.put("insertChecklist", new FontInfo(materialIcons, MaterialIcons.ICON.PLAYLIST_ADD_CHECK));
        fontIcons.put("stop", new FontInfo(mdl2Icons, MDL2Icons.ICON.STOP, Color.RED));
        fontIcons.put("stopPlay", new FontInfo(mdl2Icons, MDL2Icons.ICON.STOP, Color.RED));
        fontIcons.put("recorderConsole", new FontInfo(mdl2Icons, MDL2Icons.ICON.COMMAND_PROMPT));
        fontIcons.put("openApplication", new FontInfo(mdl2Icons, MDL2Icons.ICON.OPEN_IN_NEW_WINDOW));
        fontIcons.put("closeApplication", new FontInfo(mdl2Icons, MDL2Icons.ICON.DISCONNECT_DISPLAY, Color.RED));
        fontIcons.put("play", new FontInfo(mdl2Icons, MDL2Icons.ICON.PLAY_BADGE12, Color.GREEN));
        fontIcons.put("slowPlay", new FontInfo(mdl2Icons, MDL2Icons.ICON.PLAYBACK_RATE_OTHER, Color.GREEN));
        fontIcons.put("debug", new FontInfo(mdl2Icons, MDL2Icons.ICON.BUG));
        fontIcons.put("toggleBreakpoint", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.TOGGLE_SWITCH, Color.GREEN));
        fontIcons.put("resumePlaying", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.PLAY_PAUSE));
        fontIcons.put("stepInto", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_INTO));
        fontIcons.put("stepOver", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_OVER));
        fontIcons.put("stepReturn", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DEBUG_STEP_OUT));
        fontIcons.put("playerConsole", new FontInfo(mdl2Icons, MDL2Icons.ICON.COMMAND_PROMPT));
        fontIcons.put("showReport", new FontInfo(mdl2Icons, MDL2Icons.ICON.CERTIFICATE));
        fontIcons.put("extractModule", new FontInfo(mdl2Icons, MDL2Icons.ICON.SHARE));
        fontIcons.put("createDDT", new FontInfo(mdl2Icons, MDL2Icons.ICON.QUARENTINED_ITEMS));
        fontIcons.put("createDataLoop", new FontInfo(mdl2Icons, MDL2Icons.ICON.RESHARE));
        fontIcons.put("objectMapCreate", new FontInfo(mdl2Icons, MDL2Icons.ICON.GROUP_LIST));
        fontIcons.put("editObjectMap", new FontInfo(mdl2Icons, MDL2Icons.ICON.GROUP_LIST));
        fontIcons.put("editObjectMapConfiguration", new FontInfo(mdl2Icons, MDL2Icons.ICON.EXPLOIT_PROTECTION_SETTINGS));
        fontIcons.put("cut", new FontInfo(mdl2Icons, MDL2Icons.ICON.CUT));
        fontIcons.put("copy", new FontInfo(mdl2Icons, MDL2Icons.ICON.COPY));
        fontIcons.put("paste", new FontInfo(mdl2Icons, MDL2Icons.ICON.PASTE));
        fontIcons.put("undo", new FontInfo(mdl2Icons, MDL2Icons.ICON.UNDO));
        fontIcons.put("redo", new FontInfo(mdl2Icons, MDL2Icons.ICON.REDO));
        fontIcons.put("search", new FontInfo(mdl2Icons, MDL2Icons.ICON.SEARCH));
        fontIcons.put("replace", new FontInfo(mdl2Icons, MDL2Icons.ICON.ZOOM_IN));
        fontIcons.put("settings", new FontInfo(mdl2Icons, MDL2Icons.ICON.KEYBOARD_SETTINGS));
        fontIcons.put("fldr_obj", new FontInfo(mdl2Icons, MDL2Icons.ICON.OPEN_FOLDER_HORIZONTAL, Color.DARKSLATEGREY));
        fontIcons.put("fldr_closed", new FontInfo(mdl2Icons, MDL2Icons.ICON.FOLDER_HORIZONTAL, Color.DARKSLATEGREY));
        fontIcons.put("prj_obj", new FontInfo(mdl2Icons, MDL2Icons.ICON.APP_ICON_DEFAULT, Color.DARKSLATEBLUE));
        fontIcons.put("file", new FontInfo(mdl2Icons, MDL2Icons.ICON.DOCUMENT, Color.DARKSLATEGREY));
        fontIcons.put("file_obj", new FontInfo(mdl2Icons, MDL2Icons.ICON.DOCUMENT, Color.DARKSLATEGREY));
        fontIcons.put("rename", new FontInfo(mdl2Icons, MDL2Icons.ICON.RENAME));
        fontIcons.put("expandAll", new FontInfo(mdl2Icons, MDL2Icons.ICON.EXPLORE_CONTENT));
        fontIcons.put("collapseAll", new FontInfo(mdl2Icons, MDL2Icons.ICON.COLLAPSE_CONTENT));
        fontIcons.put("delete", new FontInfo(mdl2Icons, MDL2Icons.ICON.DELETE));
        fontIcons.put("remove", new FontInfo(mdl2Icons, MDL2Icons.ICON.REMOVE_FROM));
        fontIcons.put("refresh", new FontInfo(mdl2Icons, MDL2Icons.ICON.REFRESH));
        fontIcons.put("add", new FontInfo(mdl2Icons, MDL2Icons.ICON.ADD_TO));
        fontIcons.put("add-to-right", new FontInfo(mdl2Icons, MDL2Icons.ICON.FORWARD));
        fontIcons.put("up", new FontInfo(mdl2Icons, MDL2Icons.ICON.UP));
        fontIcons.put("down", new FontInfo(mdl2Icons, MDL2Icons.ICON.DOWN));
        fontIcons.put("info", new FontInfo(mdl2Icons, MDL2Icons.ICON.INFO));
        fontIcons.put("warn", new FontInfo(mdl2Icons, MDL2Icons.ICON.WARNING, Color.ORANGE));
        fontIcons.put("error", new FontInfo(mdl2Icons, MDL2Icons.ICON.ERROR_BADGE, Color.RED));
        fontIcons.put("rawRecord", new FontInfo(mdl2Icons, MDL2Icons.ICON.STORAGE_TAPE));
        fontIcons.put("console_view", new FontInfo(mdl2Icons, MDL2Icons.ICON.TABLET));
        fontIcons.put("clear", new FontInfo(mdl2Icons, MDL2Icons.ICON.ERASE_TOOL));
        fontIcons.put("export", new FontInfo(mdl2Icons, MDL2Icons.ICON.IMAGE_EXPORT));
        fontIcons.put("showreport", new FontInfo(mdl2Icons, MDL2Icons.ICON.CERTIFICATE));
        fontIcons.put("show_message", new FontInfo(mdl2Icons, MDL2Icons.ICON.MESSAGE));
        fontIcons.put("et", new FontInfo(mdl2Icons, MDL2Icons.ICON.E_S_I_M_BUSY));
        fontIcons.put("newModule", new FontInfo(mdl2Icons, MDL2Icons.ICON.NEW_FOLDER));
        fontIcons.put("newFixture", new FontInfo(mdl2Icons, MDL2Icons.ICON.TYPE));
        fontIcons.put("newModuleDir", new FontInfo(mdl2Icons, MDL2Icons.ICON.TREE_FOLDER_FOLDER_OPEN));
        fontIcons.put("newCheckList", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHECK_LIST));
        fontIcons.put("exit", new FontInfo(mdl2Icons, MDL2Icons.ICON.POWER_BUTTON));
        fontIcons.put("preferences", new FontInfo(mdl2Icons, MDL2Icons.ICON.PLAYER_SETTINGS));
        fontIcons.put("selectFixture", new FontInfo(mdl2Icons, MDL2Icons.ICON.TYPE));
        fontIcons.put("empty", new FontInfo(SPACE));
        fontIcons.put("clearAllBreakpoints", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.TOGGLE_SWITCH_OFF));
        fontIcons.put("projectSettings", new FontInfo(mdl2Icons, MDL2Icons.ICON.SETTING));
        fontIcons.put("manageChecklists", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHECK_LIST));
        fontIcons.put("Object Map Server...", new FontInfo(mdl2Icons, MDL2Icons.ICON.NETWORK));
        fontIcons.put("omapServer", new FontInfo(fontAwesome, FontAwesome.ICON.MAP_O));
        fontIcons.put("Welcome Message", new FontInfo(mdl2Icons, MDL2Icons.ICON.HOME));
        fontIcons.put("Microsoft Edge", new FontInfo(fontAwesome, FontAwesome.ICON.EDGE));
        fontIcons.put("Firefox", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Firefox (Marionette)", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Opera", new FontInfo(fontAwesome, FontAwesome.ICON.OPERA));
        fontIcons.put("Firefox", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Chrome", new FontInfo(fontAwesome, FontAwesome.ICON.CHROME));
        fontIcons.put("Firefox (Record)", new FontInfo(fontAwesome, FontAwesome.ICON.FIREFOX));
        fontIcons.put("Chrome (Record)", new FontInfo(fontAwesome, FontAwesome.ICON.CHROME));
        fontIcons.put("Internet Explorer", new FontInfo(fontAwesome, FontAwesome.ICON.INTERNET_EXPLORER));
        fontIcons.put("resetWorkspace", new FontInfo(mdl2Icons, MDL2Icons.ICON.RESET_DEVICE));
        fontIcons.put("releaseNotes", new FontInfo(mdl2Icons, MDL2Icons.ICON.KNOWLEDGE_ARTICLE));
        fontIcons.put("changeLog", new FontInfo(mdl2Icons, MDL2Icons.ICON.START_POINT));
        fontIcons.put("visitWebsite", new FontInfo(mdl2Icons, MDL2Icons.ICON.WEBSITE));
        fontIcons.put("helpAbout", new FontInfo(mdl2Icons, MDL2Icons.ICON.HELP));
        fontIcons.put("newIssueFile", new FontInfo(mdl2Icons, MDL2Icons.ICON.S_I_M_ERROR));
        fontIcons.put("tissue", new FontInfo(mdl2Icons, MDL2Icons.ICON.S_I_M_ERROR));
        fontIcons.put("newStoryFile", new FontInfo(mdl2Icons, MDL2Icons.ICON.E_S_I_M_NO_PROFILE));
        fontIcons.put("tstory", new FontInfo(mdl2Icons, MDL2Icons.ICON.E_S_I_M_NO_PROFILE));
        fontIcons.put("newFeatureFile", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MOVIE));
        fontIcons.put("tfeature", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.MOVIE));
        fontIcons.put("newSuiteFile", new FontInfo(materialIcons, MaterialIcons.ICON.VIDEO_LIBRARY));
        fontIcons.put("tsuite", new FontInfo(materialIcons, MaterialIcons.ICON.VIDEO_LIBRARY));
        fontIcons.put("duplicate-row", new FontInfo(mdl2Icons, MDL2Icons.ICON.ADD_SURFACE_HUB));
        fontIcons.put("header", new FontInfo(mdl2Icons, MDL2Icons.ICON.SET_TILE));
        fontIcons.put("checklist", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHECKBOX_COMPOSITE14));
        fontIcons.put("textbox", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHARACTERS));
        fontIcons.put("expandall", new FontInfo(mdl2Icons, MDL2Icons.ICON.EXPLORE_CONTENT));
        fontIcons.put("collapseall", new FontInfo(mdl2Icons, MDL2Icons.ICON.COLLAPSE_CONTENT));
        fontIcons.put("clearSearch", new FontInfo(mdl2Icons, MDL2Icons.ICON.ZOOM_OUT));
        fontIcons.put("list", new FontInfo(mdl2Icons, MDL2Icons.ICON.BULLETED_LIST));
        fontIcons.put("open", new FontInfo(mdl2Icons, MDL2Icons.ICON.OPEN_WITH));
        fontIcons.put("params", new FontInfo(mdl2Icons, MDL2Icons.ICON.BULLETED_LIST));
        fontIcons.put("table", new FontInfo(mdl2Icons, MDL2Icons.ICON.GRID_VIEW));
        fontIcons.put("data_loop", new FontInfo(mdl2Icons, MDL2Icons.ICON.RESHARE));
        fontIcons.put("script", new FontInfo(SPACE));
        fontIcons.put("convert_command", new FontInfo(SPACE));
        fontIcons.put("extract_module", new FontInfo(mdl2Icons, MDL2Icons.ICON.SHARE));
        fontIcons.put("extract", new FontInfo(SPACE));
        fontIcons.put("Save as Default", new FontInfo(mdl2Icons, MDL2Icons.ICON.SAVE_AS));
        fontIcons.put("saveas", new FontInfo(mdl2Icons, MDL2Icons.ICON.SAVE_AS));
        fontIcons.put("loaddefaults", new FontInfo(mdl2Icons, MDL2Icons.ICON.UPLOAD));
        fontIcons.put("testrunner", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.RUN));
        fontIcons.put("nextFailure", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHEVRON_DOWN_MED));
        fontIcons.put("prevFailure", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHEVRON_UP_MED));
        fontIcons.put("report", new FontInfo(mdl2Icons, MDL2Icons.ICON.CERTIFICATE));
        fontIcons.put("trace", new FontInfo(mdl2Icons, MDL2Icons.ICON.GO_TO_MESSAGE));
        fontIcons.put("run", new FontInfo(mdl2Icons, MDL2Icons.ICON.PLAY_BADGE12));
        fontIcons.put("runSelected", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.RUN));
        fontIcons.put("failure", new FontInfo(mdl2Icons, MDL2Icons.ICON.S_I_M_ERROR));
        fontIcons.put("failures", new FontInfo(mdl2Icons, MDL2Icons.ICON.S_I_M_ERROR));
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
        fontIcons.put("favourite", new FontInfo(mdl2Icons, MDL2Icons.ICON.FAVORITE_STAR_FILL));
        fontIcons.put("removeHistory", new FontInfo(mdl2Icons, MDL2Icons.ICON.REMOVE));
        fontIcons.put("clearSavedHistory", new FontInfo(mdl2Icons, MDL2Icons.ICON.FAVORITE_STAR));
        fontIcons.put("clearUnSavedHistory", new FontInfo(mdl2Icons, MDL2Icons.ICON.REMOVE_FROM));
        fontIcons.put("addfolder", new FontInfo(mdl2Icons, MDL2Icons.ICON.NEW_FOLDER));
        fontIcons.put("open-in-browser", new FontInfo(mdl2Icons, MDL2Icons.ICON.GLOBE));
        fontIcons.put("insert", new FontInfo(mdl2Icons, MDL2Icons.ICON.CHECK_LIST));
        fontIcons.put("cleanObjectMapFolders", new FontInfo(mdl2Icons, MDL2Icons.ICON.UNSYNC_FOLDER));
        fontIcons.put("csvFile", new FontInfo(fontAwesome, FontAwesome.ICON.FILE_EXCEL_O));
        fontIcons.put("addjar", new FontInfo(icoMoon, IcoMoon.ICON.FILE_ZIP));
        fontIcons.put("credits", new FontInfo(mdl2Icons, MDL2Icons.ICON.LIKE));
        fontIcons.put("properties", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.DOTS_HORIZONTAL));
        fontIcons.put("goto", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.CURSOR_DEFAULT_OUTLINE));
        fontIcons.put("screencapture", new FontInfo(fontAwesome, FontAwesome.ICON.PICTURE_O));
        fontIcons.put("next", new FontInfo(mdl2Icons, MDL2Icons.ICON.ARROW_RIGHT8));
        fontIcons.put("prev", new FontInfo(mdl2Icons, MDL2Icons.ICON.ARROW_LEFT8));
        fontIcons.put("help", new FontInfo(mdl2Icons, MDL2Icons.ICON.HELP));
        fontIcons.put("rotate", new FontInfo(mdl2Icons, MDL2Icons.ICON.ROTATE));
        fontIcons.put("Safari", new FontInfo(fontAwesome, FontAwesome.ICON.SAFARI));
        fontIcons.put("PhantomJS", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.GHOST));
        fontIcons.put("wordWrap", new FontInfo(materialDesignIcons, MaterialDesignIcons.ICON.WRAP));
        fontIcons.put("close", new FontInfo(mdl2Icons, MDL2Icons.ICON.REMOVE_FROM));
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

    public static File showMarathonSaveFileChooser(MarathonFileChooserInfo fileChooserInfo, String subTitle, Node icon) {
        MarathonFileChooser marathonFileChooser = new MarathonFileChooser(fileChooserInfo, subTitle, icon);
        marathonFileChooser.getStage().showAndWait();
        return fileChooserInfo.getSavedFile();
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
