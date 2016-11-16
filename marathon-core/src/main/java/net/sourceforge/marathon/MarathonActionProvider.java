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
package net.sourceforge.marathon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import net.sourceforge.marathon.display.DisplayWindow;
import net.sourceforge.marathon.display.IActionProvider;
import net.sourceforge.marathon.display.IMarathonAction;
import net.sourceforge.marathon.display.MarathonAction;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fx.display.AboutStage;
import net.sourceforge.marathon.fx.display.VersionInfo;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.util.Blurb;

public class MarathonActionProvider implements IActionProvider {

    private Preferences prefs = Preferences.userNodeForPackage(Constants.class);

    public static class SeparatorAction extends MarathonAction {
        public SeparatorAction(String menuName, boolean toolbar, boolean menu) {
            super(menuName, null, "", null, toolbar, menu);
        }

        @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                int endCaretPosition, int startLine) throws Exception {
        }

        @Override public boolean isSeperator() {
            return true;
        }

    }

    private IMarathonAction[] actions;

    public MarathonActionProvider(IEditorProvider editorProvider) {
        boolean iteBlurbs = Boolean.parseBoolean(prefs.get(Constants.PREF_ITE_BLURBS, "false"));
        if (iteBlurbs) {
            MarathonAction[] mactions = new MarathonAction[1];
            mactions[0] = new MarathonAction("Welcome Message", "Show the welcome message", "", editorProvider, false, true) {
                @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                        int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                    WelcomeMessage.showWelcomeMessage();
                }
            };
            mactions[0].setMenuName("File");
            actions = new IMarathonAction[mactions.length];
            for (int i = 0; i < mactions.length; i++) {
                actions[i] = mactions[i];
            }
            return;
        }
        List<MarathonAction> mactions = new ArrayList<>();
        MarathonAction maction = new MarathonAction("extractModule", "Extract into a module method", "", editorProvider, true,
                true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/extract-module", "Refactoring - Extracting a Module") {
                };
            }

        };
        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+M");
        mactions.add(maction);
        maction = new MarathonAction("createDDT", "Convert to a data driven test", "", editorProvider, true, true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/create-ddt", "Refactoring - Create DDT") {
                };
            }
        };

        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+D");
        mactions.add(maction);
        maction = new MarathonAction("createDataLoop", "Convert to a loop that uses data file", "", editorProvider, true, true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/create-data-loop", "Refactoring - Create Data Loop") {
                };
            }
        };
        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+L");
        mactions.add(maction);
        maction = new SeparatorAction("Refactor", true, false);
        mactions.add(maction);
        maction = new MarathonAction("objectMapCreate", "Create/modify the object map using the application", "", editorProvider,
                true, true, "Create Object Map") {

            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/create-object-map", "Creating a Object Map") {
                };
            }
        };
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
//        mactions.add(maction);
        maction = new MarathonAction("editObjectMap", "Modify the recognition properties for objects", "", editorProvider, true,
                true) {

            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/edit-object-map", "Edit Object Map Entries") {
                };
            }
        };
        maction.setAccelKey("^S+O");
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
        mactions.add(maction);
        maction = new MarathonAction("editObjectMapConfiguration", "Modify the object map configuration", "", editorProvider, true,
                true) {

            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/edit-object-map-configuration", "Edit Object Map Configuration") {
                };
            }
        };
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
        mactions.add(maction);
        maction = new MarathonAction("Clean Up", "Clean up the object map", "", editorProvider, false, true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
            }

            @Override public boolean isPopupMenu() {
                return true;
            }

            @Override public Menu getPopupMenu() {
                Menu menu = new Menu("Cleanup");
                menu.setGraphic(FXUIUtils.getIcon("refresh"));
                CheckMenuItem enableCleanup = new CheckMenuItem("Enable cleanup options...", FXUIUtils.getIcon("warn"));
                enableCleanup.setOnAction((e) -> {
                    new Blurb("about/clean-object-map", "Clean Object Map") {
                    };
                });
                menu.getItems().add(enableCleanup);
                MenuItem markAll = new MenuItem("Mark all components as unused", FXUIUtils.getIcon("error"));
                markAll.setOnAction((e) -> {
                    new Blurb("about/clean-object-map", "Clean Object Map") {
                    };
                });
                menu.getItems().add(markAll);
                final MenuItem startMarking = new CheckMenuItem("Start marking used components", FXUIUtils.getIcon("ok"));
                startMarking.setOnAction((e) -> {
                    new Blurb("about/clean-object-map", "Clean Object Map") {
                    };
                });
                menu.getItems().add(startMarking);
                MenuItem removeUnused = new MenuItem("Remove unused entries");
                removeUnused.setOnAction((e) -> {
                    new Blurb("about/clean-object-map", "Clean Object Map") {
                    };
                });
                menu.getItems().add(removeUnused);
                menu.getItems().add(new SeparatorMenuItem());
                MenuItem cleanDir = new MenuItem("Clean object map folder");
                cleanDir.setOnAction((e) -> {
                    new Blurb("about/clean-object-map", "Clean Object Map") {
                    };
                });
                menu.getItems().add(cleanDir);
                return menu;
            }
        };
        maction.setMenuName("Object Map");
        mactions.add(maction);
        maction = new MarathonAction("Object Map Server...", "Use remote object map server", "", editorProvider, false, true) {

            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/object-map-server", "Use Remote Object Map Server") {
                };
            }
        };
        maction.setMenuName("Object Map");
        mactions.add(maction);
        maction = new MarathonAction("Welcome Message", "Show the welcome message", "", editorProvider, false, true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                WelcomeMessage.showWelcomeMessage();
            }
        };
        maction.setMenuName("File");
        mactions.add(maction);
        maction = new MarathonAction("helpAbout", "About Marathon", "", editorProvider, false, true, "About...") {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                AboutStage aboutStage = new AboutStage(new VersionInfo(Version.id(), Version.blurbTitle(), Version.blurbCompany(),
                        Version.blurbWebsite(), Version.blurbCredits()));
                aboutStage.getStage().showAndWait();
            }
        };
        maction.setMenuName("Help");
        mactions.add(maction);
        addBrowserSelectionActions(editorProvider, mactions);
        actions = new IMarathonAction[mactions.size()];
        for (int i = 0; i < mactions.size(); i++) {
            actions[i] = mactions.get(i);
        }
    }

    public void addBrowserSelectionActions(IEditorProvider editorProvider, List<MarathonAction> mactions) {
        if (Constants.getFramework().equals(Constants.FRAMEWORK_WEB)) {
            Properties props = new Properties();
            try {
                props.load(this.getClass().getResourceAsStream("/browsers"));
                Set<Object> keys = props.keySet();
                final ToggleGroup toggleGroup = new ToggleGroup();
                for (Object proxyClass : keys) {
                    final String proxyClassName = (String) proxyClass;
                    final String browserName = props.getProperty(proxyClassName);
                    MarathonAction baction = new MarathonAction(browserName, "Use " + browserName, "", editorProvider, false,
                            true) {
                        @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                                int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                            net.sourceforge.marathon.runtime.api.Preferences.instance().setValue("project", "browser",
                                    proxyClassName);
                        }

                        @Override public ToggleGroup getButtonGroup() {
                            return toggleGroup;
                        }
                    };
                    baction.setMenuName("Browser");
                    mactions.add(baction);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override public IMarathonAction[] getActions() {
        return actions;
    }
}
