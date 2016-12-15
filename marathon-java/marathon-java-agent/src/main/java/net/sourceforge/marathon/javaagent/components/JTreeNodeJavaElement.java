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
package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.JavaElementFactory;
import net.sourceforge.marathon.javaagent.NoSuchElementException;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class JTreeNodeJavaElement extends AbstractJavaElement implements IPseudoElement {

    private final static Logger logger = Logger.getLogger(JTreeNodeJavaElement.class.getName());

    private JTreeJavaElement parent;
    private int viewRow;

    public JTreeNodeJavaElement(JTreeJavaElement parent, int row) {
        super(parent);
        this.parent = parent;
        this.viewRow = row;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "nth-node").put("parameters", new JSONArray().put(viewRow + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("editor")) {
            Component editor = getEditor();
            if (editor == null) {
                throw new UnsupportedCommandException("Unable to find editingComponent for the tree. tree.editable = "
                        + ((JTree) parent.getComponent()).isEditable(), null);
            }
            return Arrays.asList(JavaElementFactory.createElement(editor, getDriver(), getWindow()));
        }
        throw new UnsupportedCommandException("JTree node does not support pseudoelement " + selector, null);
    }

    private Component getEditor() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                JTree tree = (JTree) parent.getComponent();
                TreePath pathForRow = tree.getPathForRow(viewRow);
                tree.startEditingAtPath(pathForRow);
                Component c = getEditingComponent(tree);
                if (c instanceof JComponent) {
                    ((JComponent) c).putClientProperty("marathon.celleditor", true);
                }
                return c;
            }

            private Component getEditingComponent(JTree tree) {
                TreeUI ui = tree.getUI();
                Field cField = null;
                if (ui instanceof BasicTreeUI) {
                    try {
                        cField = BasicTreeUI.class.getDeclaredField("editingComponent");
                        cField.setAccessible(true);
                        return (Component) cField.get(ui);
                    } catch (Throwable t) {
                        // TODO Auto-generated catch block
                        logger.warning("Unable to find editingComponent for " + ui.getClass());
                        t.printStackTrace();
                    } finally {
                        if (cField != null) {
                            cField.setAccessible(false);
                        }
                    }
                }
                return null;
            }
        });
    }

    @Override public IJavaElement getParent() {
        return parent;
    }

    @Override public Component getPseudoComponent() {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                validateRow();
                JTree tree = (JTree) parent.getComponent();
                TreeCellRenderer cellRenderer = tree.getCellRenderer();
                TreePath pathForRow = tree.getPathForRow(viewRow);
                Object lastPathComponent = pathForRow.getLastPathComponent();
                return cellRenderer.getTreeCellRendererComponent(tree, lastPathComponent, false, false, false, viewRow, false);
            }
        });
    }

    private void validateRow() {
        int rowCount = ((JTree) parent.getComponent()).getRowCount();
        if (viewRow >= 0 && viewRow < rowCount) {
            return;
        }
        throw new NoSuchElementException("Invalid row for JTree: (" + viewRow + ")", null);
    }

    @Override public void _moveto() {
        validateRow();
        Rectangle bounds = getCellBounds();
        getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    private Rectangle getCellBounds() {
        Callable<Rectangle> x = new Callable<Rectangle>() {
            @Override public Rectangle call() {
                JTree tree = (JTree) parent.getComponent();
                return tree.getRowBounds(viewRow);
            }
        };
        try {
            return EventQueueWait.exec(x);
        } catch (Exception e) {
            return new Rectangle();
        }
    }

    @Override public boolean _isDisplayed() {
        return isVisible((JTree) parent.getComponent(), viewRow);
    }

    private boolean isVisible(JTree tree, int row) {
        Rectangle visibleRect = tree.getVisibleRect();
        Rectangle cellRect = tree.getRowBounds(row);
        return SwingUtilities.isRectangleContainingRectangle(visibleRect, cellRect);
    }

    @Override public Point _getMidpoint() {
        validateRow();
        Rectangle bounds = getCellBounds();
        return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    }

    public boolean isExpanded() {
        return EventQueueWait.exec(new Callable<Boolean>() {
            @Override public Boolean call() throws Exception {
                JTree tree = (JTree) parent.getComponent();
                return tree.isExpanded(viewRow);
            }
        });
    }

    public String escapeSpecialCharacters(String name) {
        return name.replaceAll("/", "\\\\/");
    }

    public int getRow() {
        return viewRow;
    }

    public int getViewRow() {
        return viewRow + 1;
    }

    @Override public void submit() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ((JTree) parent.getComponent()).stopEditing();
            }
        });
    }
}
