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
package net.sourceforge.marathon.editor.rsta;

import java.awt.event.ActionEvent;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.util.PropertyEditor;

public class RSTAEditorProvider implements IEditorProvider, PreferenceChangeListener {

    private boolean tabConversion;
    private int tabSize;

    public RSTAEditorProvider() {
        readPreferences();
        installPreferenceListener();

    }

    private void installPreferenceListener() {
        Preferences preferences = Preferences.userNodeForPackage(IEditor.class);
        preferences.addPreferenceChangeListener(this);
    }

    private void readPreferences() {
        Preferences preferences = Preferences.userNodeForPackage(IEditorProvider.class);
        String prop = preferences.get("editor.tabconversion", "true");
        tabConversion = Boolean.parseBoolean(prop);
        prop = preferences.get("editor.tabsize", "4");
        tabSize = Integer.parseInt(prop);
    }

    public boolean getTabConversion() {
        return tabConversion;
    }

    public int getTabSize() {
        return tabSize;
    }

    public void changeEditorSettings(JFrame parent) {
        PropertyEditor ped = new PropertyEditor(parent, RSTAEditorProvider.class,
                RSTAEditorProvider.class.getResource("rsyntaxtextarea.props"), "Editor Settings");
        ped.setVisible(true);
    }

    public void changeShortcuts(JFrame parent) {
        JOptionPane.showMessageDialog(parent, "Not yet implemented...");
    }

    public void preferenceChange(PreferenceChangeEvent evt) {
        String key = evt.getKey();
        String value = evt.getNewValue();
        if ("editor.tabconversion".equals(key))
            tabConversion = Boolean.parseBoolean(value);
        else if ("editor.tabsize".equals(key))
            tabSize = Integer.parseInt(value);
    }

    public boolean isEditorSettingsAvailable() {
        return true;
    }

    public boolean isEditorShortcutKeysAvailable() {
        return false;
    }

    public IEditor get(boolean linenumbers, int startLineNumber, EditorType type) {
        RSTAEditor rstaEditor = new RSTAEditor(linenumbers, startLineNumber);
        rstaEditor.setTabsEmulated(getTabConversion());
        rstaEditor.setTabSize(getTabSize());
        return rstaEditor;
    }

    public JMenuItem getEditorSettingsMenuItem(final JFrame parent) {
        return new JMenuItem(new AbstractAction("Marathon Editor Settings...", RSTAEditor.EMPTY_ICON) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                changeEditorSettings(parent);
            }
        });
    }

    public JMenuItem getEditorShortcutMenuItem(final JFrame parent) {
        return new JMenuItem(new AbstractAction("Marathon Editor Shorcuts...", RSTAEditor.EMPTY_ICON) {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                changeShortcuts(parent);
            }
        });
    }

    public boolean supports(EditorType type) {
        if (type == EditorType.CSV || type == EditorType.SUITE)
            return false;
        return true;
    }

}
