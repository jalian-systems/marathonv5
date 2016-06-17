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
package net.sourceforge.marathon.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class MultiEditorProvider implements IEditorProvider {
    public static final ImageIcon EMPTY_ICON = new ImageIcon(MultiEditorProvider.class.getResource("empty.gif"));

    private List<IEditorProvider> providers = new ArrayList<IEditorProvider>();
    private IEditorProvider defaultProvider;

    public IEditor get(boolean linenumbers, int startLineNumber, EditorType type) {
        if (type == IEditorProvider.EditorType.OTHER) {
            return defaultProvider.get(linenumbers, startLineNumber, type);
        }
        return findProvider(type).get(linenumbers, startLineNumber, type);
    }

    private IEditorProvider findProvider(EditorType type) {
        for (IEditorProvider provider : providers) {
            if (provider.supports(type))
                return provider;
        }
        return defaultProvider;
    }

    public boolean getTabConversion() {
        return defaultProvider.getTabConversion();
    }

    public int getTabSize() {
        return defaultProvider.getTabSize();
    }

    public boolean isEditorSettingsAvailable() {
        for (IEditorProvider provider : providers) {
            if (provider.isEditorSettingsAvailable())
                return true;
        }
        return false;
    }

    public boolean isEditorShortcutKeysAvailable() {
        for (IEditorProvider provider : providers) {
            if (provider.isEditorShortcutKeysAvailable())
                return true;
        }
        return false;
    }

    public JMenuItem getEditorSettingsMenuItem(JFrame parent) {
        List<JMenuItem> items = new ArrayList<JMenuItem>();
        for (IEditorProvider provider : providers) {
            if (provider.isEditorSettingsAvailable())
                items.add(provider.getEditorSettingsMenuItem(parent));
        }
        if (items.size() == 0)
            return null;
        if (items.size() == 1)
            return items.get(0);
        JMenu menu = new JMenu("Editor Settings");
        menu.setIcon(EMPTY_ICON);
        for (JMenuItem item : items) {
            menu.add(item);
        }
        return menu;
    }

    public JMenuItem getEditorShortcutMenuItem(JFrame parent) {
        List<JMenuItem> items = new ArrayList<JMenuItem>();
        for (IEditorProvider provider : providers) {
            if (provider.isEditorShortcutKeysAvailable())
                items.add(provider.getEditorShortcutMenuItem(parent));
        }
        if (items.size() == 0)
            return null;
        if (items.size() == 1)
            return items.get(0);
        JMenu menu = new JMenu("Editor Shortcuts");
        menu.setIcon(EMPTY_ICON);
        for (JMenuItem item : items) {
            menu.add(item);
        }
        return menu;
    }

    public boolean supports(EditorType type) {
        throw new UnsupportedOperationException("Multi editor provider can't support supports");
    }

    public void add(IEditorProvider provider, boolean b) {
        providers.add(provider);
        if (b)
            defaultProvider = provider;
    }

}
