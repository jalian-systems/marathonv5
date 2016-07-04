/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import net.sourceforge.marathon.display.DisplayWindow;
import net.sourceforge.marathon.display.IActionProvider;
import net.sourceforge.marathon.display.IMarathonAction;
import net.sourceforge.marathon.display.MarathonAction;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IScriptModel;
import net.sourceforge.marathon.runtime.api.UIUtils;
import net.sourceforge.marathon.util.Blurb;

public class MarathonActionProvider implements IActionProvider {

    public static final Icon EMPTY_ICON = new ImageIcon(MarathonActionProvider.class.getResource("empty.gif"));
    public static final Icon OK_ICON = new ImageIcon(MarathonActionProvider.class.getResource("ok.gif"));
    public static final Icon ERROR_ICON = new ImageIcon(MarathonActionProvider.class.getResource("error.gif"));
    public static final Icon REFRESH_ICON = new ImageIcon(MarathonActionProvider.class.getResource("refresh.gif"));
    public static final Icon CLEAR_ICON = new ImageIcon(MarathonActionProvider.class.getResource("clear.gif"));
    private Preferences prefs = Preferences.userNodeForPackage(Constants.class);

    public static class SeparatorAction extends MarathonAction {
        public SeparatorAction(String menuName, boolean toolbar, boolean menu) {
            super(menuName, null, (char) 0, null, null, null, toolbar, menu);
        }

        public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
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
            mactions[0] = new MarathonAction("Welcome Message", "Show the welcome message", (char) 0, null, null, editorProvider,
                    false, true) {
                public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                        int endCaretPosition, int startLine) throws Exception {
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
        MarathonAction maction = new MarathonAction("Extract Module", "Extract into a module method", (char) 0, UIUtils.ICON_MODULE,
                null, editorProvider, true, true) {
            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/extract-module", "Refactoring - Extracting a Module") {
                };
            }

        };
        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+M");
        mactions.add(maction);
        maction = new MarathonAction("Create DDT", "Convert to a data driven test", (char) 0, UIUtils.ICON_CONVERT, null,
                editorProvider, true, true) {
            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/create-ddt", "Refactoring - Create DDT") {
                };
            }
        };

        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+D");
        mactions.add(maction);
        maction = new MarathonAction("Create Data Loop", "Convert to a loop that uses data file", (char) 0, UIUtils.ICON_LOOP, null,
                editorProvider, true, true) {
            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int startOffset,
                    int endOffset, int startLine) throws Exception {
                new Blurb("about/create-data-loop", "Refactoring - Create Data Loop") {
                };
            }
        };
        maction.setMenuName("Refactor");
        maction.setMenuMnemonic('R');
        maction.setAccelKey("^S+L");
        maction = new SeparatorAction("Refactor", true, false);
        mactions.add(maction);
        maction = new MarathonAction("Create object map...", "Create/modify the object map using the application", (char) 0,
                UIUtils.ICON_OMAP_CREATE, null, editorProvider, true, true) {

            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                    int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/create-object-map", "Creating a Object Map") {
                };
            }
        };
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
        mactions.add(maction);
        maction = new MarathonAction("Edit Object Map...", "Modify the recognition properties for objects", (char) 0,
                UIUtils.ICON_OBJECTMAP, null, editorProvider, true, true) {

            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                    int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/edit-object-map", "Edit Object Map Entries") {
                };
            }
        };
        maction.setAccelKey("^S+O");
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
        mactions.add(maction);
        maction = new MarathonAction("Edit Object Map Configuration...", "Modify the object map configuration", (char) 0,
                UIUtils.ICON_CONF_EDIT, null, editorProvider, true, true) {

            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                    int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/edit-object-map-configuration", "Edit Object Map Configuration") {
                };
            }
        };
        maction.setMenuName("Object Map");
        maction.setMenuMnemonic('O');
        mactions.add(maction);
        maction = new MarathonAction("Clean Up", "Clean up the object map", (char) 0, null, null, editorProvider, false, true) {
            @Override public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                    int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
            }

            @Override public boolean isPopupMenu() {
                return true;
            }

            @Override public JMenu getPopupMenu() {
                JMenu menu = new JMenu("Cleanup");
                menu.setIcon(REFRESH_ICON);
                JMenuItem markAll = new JMenuItem(new AbstractAction("Mark all components as unused", ERROR_ICON) {
                    private static final long serialVersionUID = 1L;

                    @Override public void actionPerformed(ActionEvent e) {
                        new Blurb("about/clean-object-map", "Clean Object Map") {
                        };
                    }
                });
                menu.add(markAll);
                final JMenuItem startMarking = new JCheckBoxMenuItem();
                startMarking.setAction(new AbstractAction("Start marking used components", OK_ICON) {
                    private static final long serialVersionUID = 1L;

                    @Override public void actionPerformed(ActionEvent e) {
                        new Blurb("about/clean-object-map", "Clean Object Map") {
                        };
                    }
                });
                menu.add(startMarking);
                JMenuItem removeUnused = new JMenuItem(new AbstractAction("Remove all unused object map entries", CLEAR_ICON) {
                    private static final long serialVersionUID = 1L;

                    @Override public void actionPerformed(ActionEvent e) {
                        new Blurb("about/clean-object-map", "Clean Object Map") {
                        };
                    }
                });
                menu.add(removeUnused);
                menu.add(new JSeparator());
                JMenuItem cleanDir = new JMenuItem(new AbstractAction("Clean Object Map folder...", EMPTY_ICON) {
                    private static final long serialVersionUID = 1L;

                    @Override public void actionPerformed(ActionEvent e) {
                        new Blurb("about/clean-object-map", "Clean Object Map") {
                        };
                    }
                });
                menu.add(cleanDir);
                return menu;
            }
        };
        maction.setMenuName("Object Map");
        mactions.add(maction);
        maction = new MarathonAction("Object Map Server...", "Use remote object map server", (char) 0, UIUtils.ICON_CONF_EDIT, null,
                editorProvider, false, true) {

            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                    int endCaretPosition, int startLine) throws Exception {
                new Blurb("about/object-map-server", "Use Remote Object Map Server") {
                };
            }
        };
        maction.setMenuName("Object Map");
        mactions.add(maction);
        maction = new MarathonAction("Welcome Message", "Show the welcome message", (char) 0, null, null, editorProvider, false,
                true) {
            public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script, int beginCaretPostion,
                    int endCaretPosition, int startLine) throws Exception {
                WelcomeMessage.showWelcomeMessage();
            }
        };
        maction.setMenuName("File");
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
                final ButtonGroup buttonGroup = new ButtonGroup();
                for (Object proxyClass : keys) {
                    final String proxyClassName = (String) proxyClass;
                    final String browserName = props.getProperty(proxyClassName);
                    MarathonAction baction = new MarathonAction(browserName, "Use " + browserName, (char) 0, null, null,
                            editorProvider, false, true) {
                        public void actionPerformed(DisplayWindow parent, IScriptModel scriptModel, String script,
                                int beginCaretPostion, int endCaretPosition, int startLine) throws Exception {
                            System.setProperty(Constants.PROP_BROWSER, proxyClassName);
                        }

                        @Override public ButtonGroup getButtonGroup() {
                            return buttonGroup;
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

    public IMarathonAction[] getActions() {
        return actions;
    }
}
