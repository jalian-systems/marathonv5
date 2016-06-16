package net.sourceforge.marathon.editor;

import javax.swing.JFrame;
import javax.swing.JMenuItem;

public interface IEditorProvider {
    static enum EditorType {
        CSV, SUITE, OTHER
    }

    boolean getTabConversion();

    int getTabSize();

    boolean isEditorSettingsAvailable();

    boolean isEditorShortcutKeysAvailable();

    JMenuItem getEditorSettingsMenuItem(JFrame parent);

    JMenuItem getEditorShortcutMenuItem(JFrame parent);

    IEditor get(boolean linenumbers, int startLineNumber, EditorType type);

    boolean supports(EditorType type);

}
