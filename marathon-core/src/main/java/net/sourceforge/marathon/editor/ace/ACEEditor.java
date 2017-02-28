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
package net.sourceforge.marathon.editor.ace;

import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import net.sourceforge.marathon.editor.FileBasedEditor;
import net.sourceforge.marathon.editor.IContentChangeListener;
import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditorProvider.EditorType;
import net.sourceforge.marathon.editor.IStatusBar;
import net.sourceforge.marathon.fx.api.EventListenerList;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.ToolBarContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;
import net.sourceforge.marathon.fxdocking.ToolBarPanel;
import net.sourceforge.marathon.fxdocking.VLToolBar;
import net.sourceforge.marathon.model.Group;
import net.sourceforge.marathon.model.Group.GroupType;
import net.sourceforge.marathon.resource.Project;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IPreferenceChangeListener;
import net.sourceforge.marathon.runtime.api.Preferences;
import net.sourceforge.marathon.util.AbstractSimpleAction;
import net.sourceforge.marathon.util.INameValidateChecker;
import net.sourceforge.marathon.util.IResourceHandler;
import netscape.javascript.JSObject;

public class ACEEditor extends FileBasedEditor implements IPreferenceChangeListener, IEditor, ClipboardListener {

    private WebEngine engine;
    private Map<String, Object> data = new HashMap<>();
    private boolean showLinenumbers;
    private int startLineNumber;
    private Node node;
    private IStatusBar statusBar;
    private Map<String, AbstractSimpleAction> keyBindings = new HashMap<>();

    private EventListenerList listeners = new EventListenerList();
    private boolean dirty;
    private int caretRow;
    private int caretColumn;
    private Button pasteButton;
    private Button copyButton;
    private Button cutButton;
    private SystemClipboard clipboard;
    private Button redoButton;
    private Button undoButton;
    private WebView webView;
    private boolean editorDefined;
    private boolean withToolbar;
    private boolean setContentCalled;
    private MenuButton infoButton;

    public ACEEditor(boolean showLinenumbers, int startLineNumber, boolean withToolbar) {
        this.showLinenumbers = showLinenumbers;
        this.startLineNumber = startLineNumber;
        this.withToolbar = withToolbar;
        initClipboard();
        initComponent();
        Preferences.instance().addPreferenceChangeListener("ace-editor", this);
        keyBindings.put("^-,", new AbstractSimpleAction("settings", "Change editor settings", "", null) {
            private static final long serialVersionUID = 1L;

            @Override public void handle(ActionEvent e) {
                onSettings();
            }
        });
    }

    private void initClipboard() {
        clipboard = new SystemClipboard();
        clipboard.addListener(this);
    }

    @Override public void setStatusBar(IStatusBar statusBar) {
        this.statusBar = statusBar;
    }

    @Override public void startInserting() {
        editorExecuteProc("startInserting");
    }

    @Override public void stopInserting() {
        editorExecuteProc("stopInserting");
    }

    @Override public void insertScript(String script) {
        editorExecuteProc("insertScript", new JSONObject().put("script", script));
    }

    @Override public void addKeyBinding(String keyBinding, AbstractSimpleAction action) {
        keyBindings.put(keyBinding, action);
    }

    @Override public void highlightLine(int line) {
        editorExecuteProc("highlightLine", new JSONObject().put("row", line));
    }

    @Override public boolean isEditable() {
        return editorExecuteMethod("isEditable").getBoolean("editable");
    }

    @Override public int getSelectionStart() {
        return editorExecuteMethod("getSelectionStart").getInt("position");
    }

    @Override public int getSelectionEnd() {
        return editorExecuteMethod("getSelectionEnd").getInt("position");
    }

    @Override public void setDirty(boolean b) {
        this.dirty = b;
    }

    @Override public boolean isDirty() {
        return this.dirty;
    }

    @Override public void addCaretListener(CaretListener listener) {
        listeners.add(CaretListener.class, listener);
    }

    @Override public void addContentChangeListener(IContentChangeListener l) {
        listeners.add(IContentChangeListener.class, l);
    }

    @Override public int getCaretLine() {
        return editorExecuteMethod("getCaretLine").getInt("row");
    }

    @Override public void setCaretLine(int line) {
        editorExecuteProc("setCaretLine", new JSONObject().put("row", line));
        if (getCaretLine() != line) {
            Platform.runLater(() -> editorExecuteProc("setCaretLine", new JSONObject().put("row", line)));
        }

    }

    private void initComponent() {
        webView = new WebView();
        String externalForm = ACEEditor.class.getResource("/Ace.html").toExternalForm();
        WebEngine engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
            @Override public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
                if (newValue != State.SUCCEEDED) {
                    return;
                }
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", ACEEditor.this);
                engine.executeScript("console.log = function(message)\n" + "{\n" + "    java.log(message);\n" + "};");
                ACEEditor.this.engine = engine;
                setOptions(new JSONObject().put("showLineNumbers", showLinenumbers).put("firstLineNumber", startLineNumber)
                        .put("overwrite", false));
                loadPreferences();
                hookKeyBindings();
            }
        });
        engine.load(externalForm);
        ToolBarContainer container = ToolBarContainer.createDefaultContainer(Orientation.RIGHT);
        if (withToolbar) {
            ToolBarPanel toolBarPanel = container.getToolBarPanel();
            createToolBars(toolBarPanel);
        }
        container.setContent(webView);
        this.node = container;
    }

    public void hookKeyBindings() {
        JSONArray keysArray = new JSONArray();
        Set<Entry<String, AbstractSimpleAction>> entries = keyBindings.entrySet();
        for (Entry<String, AbstractSimpleAction> entry : entries) {
            keysArray.put(new JSONObject().put("key", entry.getKey()).put("name", entry.getValue().getName()));
        }
        JSONObject o = new JSONObject();
        o.put("keys", keysArray);
        editorExecuteProc("hookKeyBindings", o);
    }

    private void createToolBars(ToolBarPanel toolBarPanel) {
        VLToolBar infoToolBar = new VLToolBar();
        infoButton = new MenuButton(null, FXUIUtils.getIcon("info"));
        infoButton.setDisable(true);
        infoToolBar.add(infoButton);
        toolBarPanel.add(infoToolBar);
        VLToolBar clipboardToolBar = new VLToolBar();
        cutButton = FXUIUtils.createButton("cut", "Remove selected text and copy to clipboard");
        cutButton.setOnAction((event) -> cut());
        clipboardToolBar.add(cutButton);
        copyButton = FXUIUtils.createButton("copy", "Copy selected text to clipboard");
        copyButton.setOnAction((event) -> copy());
        clipboardToolBar.add(copyButton);
        pasteButton = FXUIUtils.createButton("paste", "Paste text from clipboard");
        pasteButton.setOnAction((event) -> paste());
        clipboardToolBar.add(pasteButton);
        toolBarPanel.add(clipboardToolBar);
        VLToolBar redoToolBar = new VLToolBar();
        undoButton = FXUIUtils.createButton("undo", "Undo last action");
        undoButton.setOnAction((event) -> editorExecuteProc("undo"));
        redoToolBar.add(undoButton);
        redoButton = FXUIUtils.createButton("redo", "Redo last undone action");
        redoButton.setOnAction((event) -> editorExecuteProc("redo"));
        redoToolBar.add(redoButton);
        toolBarPanel.add(redoToolBar);
        VLToolBar searchToolBar = new VLToolBar();
        Button search = FXUIUtils.createButton("search", "Search for a pattern", true);
        search.setOnAction((event) -> editorExecuteProc("find"));
        searchToolBar.add(search);
        Button replace = FXUIUtils.createButton("replace", "Search for a pattern and replace", true);
        replace.setOnAction((event) -> editorExecuteProc("replace"));
        searchToolBar.add(replace);
        toolBarPanel.add(searchToolBar);
        VLToolBar settingsToolBar = new VLToolBar();
        Button settingsButton = FXUIUtils.createButton("settings", "Modify editor settings", true);
        settingsButton.setOnAction((event) -> onSettings());
        settingsToolBar.add(settingsButton);
        toolBarPanel.add(settingsToolBar);
    }

    private void onSettings() {
        AceEditorPreferencesStage stage = new AceEditorPreferencesStage(new AceEditorPreferencesInfo(this));
        stage.setPreferenceHandler(new IAceEditorPreferenceHandler() {
            @Override public void changeTheme(AceEditorTheme n) {
                setTheme(n.getTheme());
            }

            @Override public void changeKeyboardHandler(String n) {
                setKeyboardHandler(n);
            }

            @Override public void changeTabSize(int tabSize) {
                setTabSize(tabSize);
            }

            @Override public void changeTabConversion(Boolean tabConversion) {
                setTabConversion(tabConversion);
            }

            @Override public void changeShowLineNumbers(Boolean showLineNumbers) {
                setShowLineNumbers(showLineNumbers);
            }

            @Override public void changeFontSize(String fontSize) {
                setFontSize(fontSize);
            }
        });
        stage.getStage().showAndWait();
    }

    private void paste() {
        String text = clipboard.getData();
        if (text != null) {
            editorExecuteProc("paste", new JSONObject().put("text", text));
        }
    }

    private void copy() {
        JSONObject copyData = editorExecuteMethod("copy");
        clipboard.setData(copyData.getString("text"));
    }

    private void cut() {
        JSONObject cutData = editorExecuteMethod("cut");
        clipboard.setData(cutData.getString("text"));
    }

    @Override public void addGutterListener(IGutterListener l) {
        listeners.add(IGutterListener.class, l);
    }

    @Override public Object getData(String key) {
        return data.get(key);
    }

    @Override public void setData(String key, Object value) {
        data.put(key, value);
    }

    @Override public void setCaretPosition(int position) {
        editorExecuteProc("setCaretPosition", new JSONObject().put("position", position));
    }

    @Override public int getCaretPosition() {
        return editorExecuteMethod("getCaretPosition").getInt("position");
    }

    @Override public String getText() {
        return editorExecuteMethod("getContent").getString("content");
    }

    @Override public void setText(String code) {
        JSONObject o = new JSONObject();
        o.put("line", 1);
        o.put("content", code);
        editorExecuteProc("setContent", o);
    }

    @Override public void setMode(String mode) {
        editorExecuteProc("setMode", new JSONObject().put("mode", mode));
    }

    @Override public int getLineOfOffset(int selectionStart) {
        return editorExecuteMethod("getLineOfOffset", new JSONObject().put("offset", selectionStart)).getInt("line");
    }

    @Override public int getLineStartOffset(int startLine) {
        return editorExecuteMethod("getLineStartOffset", new JSONObject().put("line", startLine)).getInt("offset");
    }

    @Override public int getLineEndOffset(int endLine) {
        return editorExecuteMethod("getLineEndOffset", new JSONObject().put("line", endLine)).getInt("offset");
    }

    @Override public void setFocus() {
        runWhenReady(() -> {
            editorExecuteProc("requestFocus");
            webView.requestFocus();
        });
    }

    @Override public void setMenuItems(MenuItem[] menuItems) {
    }

    @Override public void toggleInsertMode() {
        boolean overwrite = !editorExecuteMethod("isOverwriteEnabled").getBoolean("overwrite");
        editorExecuteProc("setOptions", new JSONObject().put("overwrite", overwrite));
        updateStatusBar();
    }

    private void updateStatusBar() {
        if (statusBar == null) {
            return;
        }
        statusBar.setCaretLocation(caretRow, caretColumn);
        statusBar.setIsOverwriteEnabled(editorExecuteMethod("isOverwriteEnabled").getBoolean("overwrite"));
    }

    @Override public void setEditable(boolean b) {
        editorExecuteProc("setEditable", new JSONObject().put("editable", b));
    }

    private void editorExecuteProc(String method) {
        editorExecuteProc(method, new JSONObject());
    }

    private void editorExecuteProc(String method, JSONObject o) {
        if (engine == null || !editorDefined()) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    editorExecuteProc(method, o);
                }
            });
            return;
        }
        JSONObject req = new JSONObject();
        req.put("method", method);
        req.put("data", o.toString());
        engine.executeScript("$editor.executeScript('" + escape(req.toString()) + "');");
        setContentCalled = "setContent".equals(method);
    }

    private boolean editorDefined() {
        if (editorDefined) {
            return editorDefined;
        }
        editorDefined = !((boolean) engine.executeScript("!window.$editor"));
        return editorDefined;
    }

    private JSONObject editorExecuteMethod(String method) {
        return editorExecuteMethod(method, new JSONObject());
    }

    private JSONObject editorExecuteMethod(String method, JSONObject o) {
        JSONObject req = new JSONObject();
        req.put("method", method);
        req.put("data", o.toString());
        if (engine == null) {
            throw new RuntimeException("Editor is not initialized for calling this method: " + method);
        }
        return new JSONObject((String) engine.executeScript("$editor.executeScript('" + escape(req.toString()) + "');"));
    }

    private String escape(String text) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = text.toCharArray();
        for (char c : charArray) {
            if (c == '\\') {
                sb.append("\\\\");
            } else if (c == '\'') {
                sb.append("\\'");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void log(String text) {
        System.out.println(text);
    }

    public void onCursorChange(int row, int col) {
        this.caretRow = row;
        this.caretColumn = col;
        updateStatusBar();
        JSONObject selection = editorExecuteMethod("getSelection");
        if (cutButton != null) {
            cutButton.setDisable(selection.getInt("start") == selection.getInt("end"));
        }
        if (copyButton != null) {
            copyButton.setDisable(selection.getInt("start") == selection.getInt("end"));
        }
    }

    public void fireContentChangeEvent() {
        setDirty(true);
        IContentChangeListener[] la = listeners.getListeners(IContentChangeListener.class);
        for (final IContentChangeListener l : la) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    l.contentChanged();
                }
            });
        }
    }

    public void fireCaretUpdate(int dot, int mark) {
        CaretListener[] la = listeners.getListeners(CaretListener.class);
        for (final CaretListener l : la) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    l.caretUpdate();
                }
            });
        }
    }

    @Override public void refresh() {
        updateStatusBar();
        editorExecuteProc("removeAllBreakPoints");
        IGutterListener[] la = listeners.getListeners(IGutterListener.class);
        int lineCount = editorExecuteMethod("getLineCount").getInt("count");
        for (IGutterListener listener : la) {
            for (int line = 0; line < lineCount; line++) {
                if (listener.hasBreakpointAtLine(line)) {
                    editorExecuteProc("setBreakPoint", new JSONObject().put("row", line));
                }
            }
        }
    }

    public void gutterDblClicked(int row) {
        IGutterListener[] la = listeners.getListeners(IGutterListener.class);
        for (IGutterListener listener : la) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    listener.gutterDoubleClickedAt(row);
                }
            });
        }

    }

    @Override public Node getNode() {
        return node;
    }

    @Override public void runWhenReady(Runnable r) {
        if (engine == null || !editorDefined()) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    runWhenReady(r);
                }
            });
            return;
        }
        r.run();
    }

    @Override public void runWhenContentLoaded(Runnable r) {
        if (engine == null || !editorDefined() || setContentCalled) {
            Platform.runLater(new Runnable() {
                @Override public void run() {
                    runWhenReady(r);
                }
            });
            return;
        }
        r.run();
    }

    @Override public Font getFont() {
        return null;
    }

    @Override public void clipboardChanged(String data) {
        Platform.runLater(() -> {
            if (pasteButton != null) {
                pasteButton.setDisable(data == null);
            }
        });
    }

    public void setUndoManagerStatus() {
        if (undoButton != null) {
            undoButton.setDisable(!canUndo());
        }
        if (redoButton != null) {
            redoButton.setDisable(!canRedo());
        }
    }

    private boolean canRedo() {
        return editorExecuteMethod("canredo").getBoolean("value");
    }

    private boolean canUndo() {
        return editorExecuteMethod("canundo").getBoolean("value");
    }

    public JSONObject getThemes() {
        return editorExecuteMethod("getThemes");
    }

    public String getSelectedTheme() {
        JSONObject r = editorExecuteMethod("getSelectedTheme");
        if (r.has("theme")) {
            return r.getString("theme");
        }
        return null;
    }

    @Override public void preferencesChanged(String section, JSONObject preferences) {
        loadPreferences(preferences);
    }

    public void loadPreferences(JSONObject preferences) {
        if (preferences.has("theme")) {
            setTheme(preferences.getString("theme"));
        } else {
            setTheme("ace/theme/cobalt");
        }
        if (preferences.has("keyboard-handler")) {
            setKeyboardHandler(preferences.getString("keyboard-handler"));
        } else {
            setKeyboardHandler("ace");
        }
        if (preferences.has("tabSize")) {
            setTabSize(preferences.getInt("tabSize"));
        } else {
            setTabSize(2);
        }
        if (preferences.has("tabConversion")) {
            setTabConversion(preferences.getBoolean("tabConversion"));
        } else {
            setTabConversion(true);
        }
        if (preferences.has("showLineNumbers")) {
            setShowLineNumbers(preferences.getBoolean("showLineNumbers"));
        } else {
            setShowLineNumbers(true);
        }
        if (preferences.has("fontSize")) {
            setFontSize(preferences.getString("fontSize"));
        } else {
            setFontSize("13px");
        }
    }

    private void loadPreferences() {
        Preferences preferences = Preferences.instance();
        loadPreferences(preferences.getSection("ace-editor"));
    }

    public void setTheme(String theme) {
        editorExecuteProc("setTheme", new JSONObject().put("theme", theme));
    }

    public String getKeyboardHandler() {
        return editorExecuteMethod("getKeyboardHandler").getString("handler");
    }

    public void setKeyboardHandler(String n) {
        editorExecuteProc("setKeyboardHandler", new JSONObject().put("handler", n));
    }

    public JSONObject getOptions() {
        return editorExecuteMethod("getOptions");
    }

    public void setOptions(JSONObject options) {
        editorExecuteProc("setOptions", options);
    }

    public void setTabSize(int tabSize) {
        setOptions(new JSONObject().put("tabSize", tabSize));
    }

    public void setFontSize(String fontSize) {
        setOptions(new JSONObject().put("fontSize", fontSize));
    }

    public void setTabConversion(Boolean tabConversion) {
        setOptions(new JSONObject().put("tabConversion", tabConversion));
    }

    public void setShowLineNumbers(Boolean showLineNumbers) {
        setOptions(new JSONObject().put("showLineNumbers", showLineNumbers));
    }

    public void onCommand(String key) {
        AbstractSimpleAction action = keyBindings.get(key);
        action.handle(new ActionEvent(this, this.node));
    }

    @Override public boolean canSaveAs() {
        return true;
    }

    @Override public IResourceHandler createResourceHandler(EditorType type, INameValidateChecker nameChecker) throws IOException {
        IResourceHandler handler = super.createResourceHandler(type, nameChecker);
        infoButton.showingProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable observable) {
                infoButton.getItems().clear();
                populateInfoMenu();
            }
        });
        Platform.runLater(() -> {
            populateInfoMenu();
        });
        return handler;
    }

    private void populateInfoMenu() {
        if (isTestFile()) {
            File currentFile = fileHandler.getCurrentFile();
            boolean disable = true;
            if (currentFile != null) {
                String testID = Project.getTestID(currentFile);
                String tmsPattern = System.getProperty(Constants.PROP_TMS_PATTERN);
                if (testID != null && !"".equals(testID)) {
                    createURLLink(tmsPattern, testID, "tag");
                    disable = false;
                }
                List<Group> groups = Group.getGroups(GroupType.ISSUE).stream().filter((g) -> g.hasTest(currentFile.toPath()))
                        .collect(Collectors.toList());
                for (Group group : groups) {
                    createURLLink(System.getProperty(Constants.PROP_ISSUE_PATTERN), group.getName(), "debug");
                    disable = false;
                }
            }
            infoButton.setDisable(disable);
        }
    }

    private void createURLLink(String pattern, String id, String icon) {
        MenuItem tmsMenuItem = new MenuItem(id, FXUIUtils.getIcon(icon));
        if (pattern != null && pattern.length() > 0) {
            String url = String.format(pattern, id);
            tmsMenuItem.setOnAction((event) -> {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        } else {
            tmsMenuItem.setDisable(true);
        }
        infoButton.getItems().add(tmsMenuItem);
    }
}
