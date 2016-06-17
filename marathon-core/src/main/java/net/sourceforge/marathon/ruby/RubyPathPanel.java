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
package net.sourceforge.marathon.ruby;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.marathon.runtime.api.ISubPropertiesPanel;
import net.sourceforge.marathon.runtime.api.ListPanel;
import net.sourceforge.marathon.runtime.api.TextPrompt;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RubyPathPanel extends ListPanel implements ISubPropertiesPanel {
    public static final String PROP_APPLICATION_RUBYPATH = "marathon.application.rubypath";
    public static final String PROP_APPLICATION_RUBYHOME = "marathon.application.rubyhome";

    private JTextField home = new JTextField();

    public RubyPathPanel(JDialog parent) {
        super(parent, true);
        TextPrompt prompt = new TextPrompt("(Bundled JRuby)", home);
        prompt.changeAlpha((float) 0.5);
    }

    public static final Icon _icon = new ImageIcon(
            RubyPathPanel.class.getClassLoader().getResource("net/sourceforge/marathon/mpf/images/cp_obj.gif"));

    public String getName() {
        return "Ruby Path";
    }

    public Icon getIcon() {
        return _icon;
    }

    public String getPropertyKey() {
        return PROP_APPLICATION_RUBYPATH;
    }

    public boolean isAddArchivesNeeded() {
        return false;
    }

    public boolean isAddClassesNeeded() {
        return false;
    }

    public boolean isAddFoldersNeeded() {
        return true;
    }

    public boolean isValidInput() {
        if (home.getText().equals(""))
            return true;
        File lib = new File(home.getText(), "lib");
        File jar;
        if (lib.exists()) {
            jar = new File(lib, "jruby.jar");
            if (jar.exists())
                return true;
        }
        int r = JOptionPane.showConfirmDialog(parent,
                "Could not find jruby.jar in give Home/lib directory. Do you want to continue?", "JRuby Home",
                JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.OK_OPTION) {
            home.requestFocusInWindow();
            return false;
        }
        return true;
    }

    public void setProperties(Properties props) {
        super.setProperties(props);
        home.setText(props.getProperty(PROP_APPLICATION_RUBYHOME, ""));
    }

    public void getProperties(Properties props) {
        super.getProperties(props);
        props.setProperty(PROP_APPLICATION_RUBYHOME, home.getText());
    }

    protected PanelBuilder getBuilder() {
        PanelBuilder builder = super.getBuilder();
        builder.appendRow("pref");
        CellConstraints constraints = new CellConstraints();
        builder.add(getHomePanel(), constraints.xyw(1, 2, 3));
        return builder;
    }

    private JPanel getHomePanel() {
        FormLayout layout = new FormLayout("pref, 3dlu, fill:pref:grow", "fill:p:grow, 3dlu, pref");
        PanelBuilder builder = new PanelBuilder(layout);
        CellConstraints constraints = new CellConstraints();
        builder.addLabel("Ru&by Home:", new CellConstraints().xyw(1, 3, 1), home, constraints.xyw(3, 3, 1));
        return builder.getPanel();
    }

    public int getMnemonic() {
        return KeyEvent.VK_B;
    }

    @Override public boolean isSingleSelection() {
        return false;
    }
}
