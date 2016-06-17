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
package net.sourceforge.marathon.navigator;

import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class NavigatorCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {
        JLabel c = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        File file = ((NavigatorTreeNode) value).getFile();
        if (file == null)
            return c;
        if (file instanceof Navigator.RootFile)
            c.setText(file.toString());
        else
            c.setText(file.getName());
        if (file instanceof Navigator.RootFile)
            c.setIcon(Icons.PROJECT);
        else if (file.isDirectory()) {
            if (expanded)
                c.setIcon(Icons.FOLDER);
            else
                c.setIcon(Icons.FOLDER_CLOSED);
        } else
            c.setIcon(Icons.FILE);
        return c;
    }
}
