package net.sourceforge.marathon.contextmenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.marathon.component.RComponent;
import net.sourceforge.marathon.component.RComponentFactory;
import net.sourceforge.marathon.contextmenu.FilterableTreeModel.Predicate;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.util.UIUtils;

public class DefaultContextMenu extends AbstractContextMenu implements IContextMenu {

    @SuppressWarnings("serial") public static class PlaceHolderTextField extends JTextField {

        private String placeholder;

        public PlaceHolderTextField(String placeholder) {
            this.placeholder = placeholder;
        }
        
        @Override protected void paintComponent(final Graphics pG) {
            super.paintComponent(pG);

            if (placeholder.length() == 0 || getText().length() > 0 || isFocusOwner()) {
                return;
            }

            final Graphics2D g = (Graphics2D) pG;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(getDisabledTextColor());
            g.drawString(placeholder, getInsets().left, pG.getFontMetrics().getMaxAscent() + getInsets().top);
        }

    }

    static class AssertionTreeNodeRenderer implements TreeCellRenderer {
        private Color bgSel;
        private Color fgSel;
        private Color bgNonSel;
        private Color fgNonSel;
        private SimpleAttributeSet valueStyle;
        private SimpleAttributeSet propertyStyle;
        private Color valueForegroundColor = new Color(0x00, 0x00, 0xa4);

        public AssertionTreeNodeRenderer() {
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
            fgSel = renderer.getTextSelectionColor();
            fgNonSel = renderer.getTextNonSelectionColor();
            bgSel = renderer.getBackgroundSelectionColor();
            bgNonSel = renderer.getBackgroundNonSelectionColor();
            valueStyle = new SimpleAttributeSet();
            StyleConstants.setForeground(valueStyle, valueForegroundColor);
            propertyStyle = new SimpleAttributeSet();
            if (fgNonSel != null)
                StyleConstants.setForeground(propertyStyle, fgNonSel);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
                int row, boolean hasFocus) {
            JTextPane pane = new JTextPane();
            if (sel) {
                pane.setBackground(bgSel);
                pane.setForeground(fgSel);
            } else {
                pane.setBackground(bgNonSel);
                pane.setForeground(fgNonSel);
            }
            AssertionTreeNode node = (AssertionTreeNode) value;
            pane.setText("");
            try {
                pane.getDocument().insertString(pane.getDocument().getLength(), node.getProperty() + " {", propertyStyle);
                pane.getDocument().insertString(pane.getDocument().getLength(),
                        node.getDisplayNode().replace("\\", "\\\\").replace("\n", "\\n").replace("\r", "\\r"), valueStyle);
                pane.getDocument().insertString(pane.getDocument().getLength(), "}", propertyStyle);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            return pane;
        }
    }

    protected static final int ASSERT_ACTION = 1;
    protected static final int WAIT_ACTION = 2;
    private JTextArea textArea;
    protected JTree assertionTree;
    private DefaultMutableTreeNode rootNode;
    protected RComponent rcomponent;
    protected FilterableTreeModel<AssertionTreeNode> assertionTreeModel;
    private JButton insertAssertionButton;
    private JButton insertWaitButton;
    protected String searchText;

    public DefaultContextMenu(ContextMenuWindow window, IJSONRecorder recorder, RComponentFactory finder) {
        super(window, recorder, finder);
    }

    public Component getContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JSplitPane splitPane = getAssertionPanel();
        JPanel buttonPanel = getButtonPanel();
        mainPanel.add(splitPane, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        insertAssertionButton = UIUtils.createInsertAssertionButton();
        insertAssertionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recordAction(ASSERT_ACTION);
            }
        });
        insertWaitButton = UIUtils.createInsertWaitButton();
        insertWaitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recordAction(WAIT_ACTION);
            }
        });
        buttonPanel.add(insertWaitButton);
        buttonPanel.add(insertAssertionButton);
        return buttonPanel;
    }

    private JSplitPane getAssertionPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        JPanel topComponent = new JPanel(new BorderLayout());
        final PlaceHolderTextField searchField = new PlaceHolderTextField("Search");
        topComponent.add(searchField, BorderLayout.NORTH);
        final JTree tree = getTree();
        JScrollPane panel = new JScrollPane(tree);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            @Override public void removeUpdate(DocumentEvent e) {
                handleSearch(searchField.getText());
            }

            @Override public void insertUpdate(DocumentEvent e) {
                handleSearch(searchField.getText());
            }

            @Override public void changedUpdate(DocumentEvent e) {
            }

            private void handleSearch(final String text) {
                if (timer != null)
                    timer.stop();
                timer = new Timer(300, new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        DefaultContextMenu.this.searchText = text;
                        ((DefaultTreeModel) assertionTreeModel.getTreeModel()).reload();
                        int n = text.split("\\.").length;
                        for (int i = 0; i < n; i++) {
                            int rowCount = tree.getRowCount();
                            for (int j = rowCount - 1; j >= 0; j--)
                                tree.expandRow(j);
                        }
                        timer = null;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
        assertionTreeModel.setPredicate(new Predicate<AssertionTreeNode>() {
            private LinkedList<AssertionTreeNode> nodeList = new LinkedList<AssertionTreeNode>();

            @Override public boolean apply(AssertionTreeNode o) {
                if (searchText == null || "".equals(searchText) || searchText.length() < 3)
                    return true;
                return isValidNode(searchText.split("\\."), findNodes(o));
            }

            protected boolean isValidNode(String[] searchFields, AssertionTreeNode[] nodes) {
                int nodeStart = tree.isRootVisible() ? 0 : 1;
                if (searchFields.length < nodes.length - nodeStart)
                    return true;
                int last = Math.min(searchFields.length - 1, nodes.length - 1 - nodeStart);
                return nodes[last + nodeStart].toString().toLowerCase().startsWith(searchFields[last].toLowerCase());
            }

            private AssertionTreeNode[] findNodes(AssertionTreeNode o) {
                nodeList.clear();
                while (o != null) {
                    nodeList.addFirst(o);
                    o = (AssertionTreeNode) o.getParent();
                }
                return nodeList.toArray(new AssertionTreeNode[nodeList.size()]);
            }

        });
        topComponent.add(panel, BorderLayout.CENTER);
        splitPane.setTopComponent(topComponent);
        textArea = new JTextArea(4, 0);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        splitPane.setBottomComponent(new JScrollPane(textArea));
        return splitPane;
    }

    private JTree getTree() {
        assertionTree = new JTree(rootNode);
        assertionTree.setRootVisible(false);
        assertionTree.setShowsRootHandles(true);
        assertionTree.setModel(getTreeModel());
        assertionTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                AssertionTreeNode lastPathComponent = (AssertionTreeNode) e.getPath().getLastPathComponent();
                textArea.setText(lastPathComponent.getDisplayValue());
            }
        });
        assertionTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                if (assertionTree.getSelectionCount() > 0) {
                    insertWaitButton.setEnabled(true);
                    insertAssertionButton.setEnabled(true);
                } else {
                    insertWaitButton.setEnabled(false);
                    insertAssertionButton.setEnabled(false);
                }
            }
        });
        assertionTree.setCellRenderer(new AssertionTreeNodeRenderer());
        assertionTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    if (assertionTree.getSelectionCount() > 0) {
                        recordAction(ASSERT_ACTION);
                    }
                }
            }
        });
        return assertionTree;
    }

    private FilterableTreeModel<AssertionTreeNode> getTreeModel() {
        getRoot();
        assertionTreeModel = new FilterableTreeModel<AssertionTreeNode>(new DefaultTreeModel(rootNode));
        return assertionTreeModel;
    }

    protected TreeNode getRoot() {
        rootNode = new AssertionTreeNode(rcomponent);
        return rootNode;
    }

    private void recordAction(int action) {
        TreePath[] selectionPaths = assertionTree.getSelectionPaths();
        for (int i = 0; i < selectionPaths.length; i++) {
            TreePath path = selectionPaths[i];
            Object[] objects = path.getPath();
            final StringBuffer sb = new StringBuffer();
            RComponent forComponent = rcomponent;
            for (int j = 1; j < objects.length; j++) {
                final AssertionTreeNode node = (AssertionTreeNode) objects[j];
                if (node.getObject() instanceof RComponent) {
                    forComponent = (RComponent) node.getObject();
                    sb.setLength(0);
                    continue;
                }
                sb.append(node.getProperty());
                if (j < objects.length - 1) {
                    if (!((AssertionTreeNode) objects[j + 1]).getProperty().startsWith("["))
                        sb.append(".");
                } else {
                    String property = sb.toString();
                    Object value = null;
                    if (property.equals("Content")) {
                        value = forComponent.getContent();
                    } else {
                        if (property.equals("Text"))
                            value = forComponent.getText();
                        else
                            value = forComponent.getAttribute(property);
                    }
                    getRecorder().recordAction(forComponent, action == ASSERT_ACTION ? "assert" : "wait", property, value);
                }
            }
        }
    }

    public void setComponent(Component component, Point point, boolean isTriggered) {
        rcomponent = getFinder().findRComponent(component, point, recorder);
        if (rcomponent == null) {
            return;
        }
        ((DefaultTreeModel) assertionTreeModel.getTreeModel()).setRoot(getRoot());
        insertWaitButton.setEnabled(false);
        insertAssertionButton.setEnabled(false);
        assertionTree.setSelectionRow(0);
    }

    public String getName() {
        return "Assertions";
    }

}
