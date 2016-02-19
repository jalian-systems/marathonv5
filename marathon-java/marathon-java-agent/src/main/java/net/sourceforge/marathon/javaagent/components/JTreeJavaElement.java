package net.sourceforge.marathon.javaagent.components;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;

import org.json.JSONArray;
import org.json.JSONObject;

public class JTreeJavaElement extends AbstractJavaElement {

    private static interface Predicate {
        public boolean isValid(JTreeNodeJavaElement e);
    }

    public JTreeJavaElement(Component component, JavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("root"))
            return Arrays.asList((IJavaElement) new JTreeNodeJavaElement(this, 0));
        else if (selector.equals("nth-node"))
            return Arrays.asList((IJavaElement) new JTreeNodeJavaElement(this, ((Integer) params[0]).intValue() - 1));
        else if (selector.equals("all-nodes") || selector.equals("all-cells")) {
            return collectNodes(new ArrayList<IJavaElement>(), new Predicate() {
                @Override public boolean isValid(JTreeNodeJavaElement e) {
                    return true;
                }
            });
        } else if (selector.equals("select-by-properties"))
            return findNodeByProperties(new JSONObject((String) params[0]));
        return super.getByPseudoElement(selector, params);
    }

    public List<IJavaElement> collectNodes(List<IJavaElement> r, Predicate p) {
        int rows = ((JTree) component).getRowCount();
        for (int i = 0; i < rows; i++) {
            JTreeNodeJavaElement e = new JTreeNodeJavaElement(this, i);
            if (p.isValid(e))
                r.add(e);
        }
        return r;
    }

    private List<IJavaElement> findNodeByProperties(JSONObject o) {
        if (!o.has("select"))
            return Collections.<IJavaElement> emptyList();
        String path = o.getString("select");
        TreePath treePath = getPath((JTree) component, path);
        int rowForPath = ((JTree) component).getRowForPath(treePath);
        if (rowForPath == -1)
            return Collections.<IJavaElement> emptyList();
        return Arrays.asList((IJavaElement) new JTreeNodeJavaElement(this, rowForPath));
    }

    @SuppressWarnings("unused") private Component getEditor(final int viewRow, final int viewCol) {
        return EventQueueWait.exec(new Callable<Component>() {
            @Override public Component call() throws Exception {
                return null;
            }
        });
    }

    @Override public String _getText() {
        int rows = ((JTree) component).getRowCount();
        JSONArray r = new JSONArray();
        for (int i = 0; i < rows; i++) {
            r.put(new JTreeNodeJavaElement(this, i)._getText());
        }
        return r.toString();
    }

    @Override public boolean marathon_select(String value) {
        Properties[] properties = PropertyHelper.fromStringToArray(value, new String[][] { { "Path" } });
        return setCellSelection(properties);
    }

    @Override public boolean marathon_select(JSONArray jsonArray) {
        List<IJavaElement> nodes = new ArrayList<IJavaElement>();
        for (int index = 0; index < jsonArray.length(); index++) {
            final Properties p = PropertyHelper.asProperties(jsonArray.getJSONObject(index));
            collectNodes(nodes, new Predicate() {
                @Override public boolean isValid(JTreeNodeJavaElement e) {
                    Enumeration<Object> keys = p.keys();
                    while (keys.hasMoreElements()) {
                        String key = (String) keys.nextElement();
                        if (!p.getProperty(key).equals(e.getAttribute(key)))
                            return false;
                    }
                    return true;
                }
            });
        }
        if (nodes.size() != jsonArray.length())
            return false;
        int[] rows = new int[nodes.size()];
        int index = 0;
        for (IJavaElement node : nodes) {
            rows[index++] = ((JTreeNodeJavaElement) node).getRow();
        }
        ((JTree) component).setSelectionRows(rows);
        return true;
    }

    private boolean setCellSelection(Properties[] properties) {
        JTree tree = (JTree) component;
        if (properties.length == 0) {
            tree.setSelectionRows(new int[0]);
            return true;
        }
        List<TreePath> paths = new ArrayList<TreePath>();
        for (int i = 0; i < properties.length; i++) {
            TreePath path = getPath(tree, properties[i].getProperty("Path"));
            if (path != null)
                paths.add(path);
        }
        if (paths.size() != properties.length)
            return false;
        tree.setSelectionPaths(paths.toArray(new TreePath[paths.size()]));
        return true;
    }

    private TreePath getPath(JTree tree, String path) {
        String[] tokens = path.substring(1).split("(?<!\\\\)/");
        TreeModel treeModel = tree.getModel();
        if (treeModel == null)
            throw new RuntimeException("Could not find model for tree");
        Object rootNode = treeModel.getRoot();
        int start = tree.isRootVisible() ? 1 : 0;
        TreePath treePath = new TreePath(rootNode);
        StringBuilder searchedPath = new StringBuilder();
        if (tree.isRootVisible()) {
            String rootNodeText = unescapeSpecialCharacters(tokens[0]);
            searchedPath.append("/" + rootNodeText);
            assertTrue("JTree does not have a root node!", rootNode != null);
            assertTrue(
                    "JTree root node does not match: Expected </" + getPathText(tree, treePath) + "> Actual: <"
                            + searchedPath.toString() + ">", searchedPath.toString().equals("/" + getPathText(tree, treePath)));
        }
        for (int i = start; i < tokens.length; i++) {
            String childText = unescapeSpecialCharacters(tokens[i]);
            searchedPath.append("/" + childText);
            boolean matched = false;
            tree.expandPath(treePath);
            for (int j = 0; j < treeModel.getChildCount(treePath.getLastPathComponent()); j++) {
                Object child = treeModel.getChild(treePath.getLastPathComponent(), j);
                TreePath childPath = treePath.pathByAddingChild(child);
                if (childText.equals(getPathText(tree, childPath))) {
                    treePath = childPath;
                    matched = true;
                    break;
                }
            }
            if (!matched)
                return null;
        }
        return treePath;
    }

    private void assertTrue(String message, boolean b) {
        if (!b)
            throw new RuntimeException(message);
    }

    public String unescapeSpecialCharacters(String name) {
        return name.replaceAll("\\\\/", "/");
    }

    private String getPathText(JTree tree, TreePath path) {
        Object lastPathComponent = path.getLastPathComponent();
        if (lastPathComponent == null)
            return "";
        return getTextForNodeObject(tree, lastPathComponent);
    }

    private String getTextForNodeObject(JTree tree, Object lastPathComponent) {
        TreeCellRenderer renderer = tree.getCellRenderer();
        if (renderer == null)
            return null;
        Component c = renderer.getTreeCellRendererComponent(tree, lastPathComponent, false, false, false, 0, false);
        if (c != null && c instanceof JLabel) {
            return ((JLabel) c).getText();
        }
        return lastPathComponent.toString();
    }

    public String getContent() {
        return new JSONArray(getContent((JTree) component)).toString();
    }

    public static String[][] getContent(JTree component) {
        TreeModel model = ((JTree) component).getModel();
        int rowCount = getNodeCount(model, model.getRoot()) + 1;
        String[][] content = new String[1][rowCount];
        List<String> treeContent = new Vector<String>(rowCount);
        getTreeContent(model, model.getRoot(), treeContent);
        treeContent.toArray(content[0]);
        return content;
    }

    private static void getTreeContent(TreeModel model, Object root, List<String> treeContent) {
        treeContent.add(root.toString());
        for (int i = 0; i < model.getChildCount(root); i++)
            getTreeContent(model, model.getChild(root, i), treeContent);
    }

    private static int getNodeCount(TreeModel model, Object root) {
        int count = model.getChildCount(root);
        for (int i = 0; i < model.getChildCount(root); i++) {
            Object node = model.getChild(root, i);
            count += getNodeCount(model, node);
        }
        return count;
    }
}
