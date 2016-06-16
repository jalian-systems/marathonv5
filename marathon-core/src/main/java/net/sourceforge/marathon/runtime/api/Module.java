package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

public class Module implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private List<Module> children;
    private List<Function> functionList;
    private DefaultMutableTreeNode rootNode;
    private final boolean file;
    private final Module parent;

    public Module(String name, Module parent) {
        this(name, false, parent);
    }

    public Module(String name, boolean file, Module parent) {
        this.file = file;
        this.name = name;
        this.parent = parent;
        children = new Vector<Module>();
        functionList = new Vector<Function>();
    }

    public void addChild(Module module) {
        children.add(module);
    }

    public Function addFunction(String fname, List<Argument> arguments, String doc) {
        Function f = new Function(fname, arguments, doc, this);
        functionList.add(f);
        return f;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Function> getFunctions() {
        return functionList;
    }

    public List<Module> getChildren() {
        return children;
    }

    @Override public String toString() {
        return name + " (children: " + children.toString() + ") (functions: " + functionList.toString() + ")";
    }

    public String getDocumentation() {
        return "";
    }

    public DefaultMutableTreeNode createTreeNode(String window) {
        rootNode = new DefaultMutableTreeNode(this);
        addModulesToTree(rootNode, this, window);
        return rootNode;
    }

    private boolean addModulesToTree(DefaultMutableTreeNode node, Module module, String window) {
        if (module.getChildren().size() == 0 && module.getFunctions().size() == 0)
            return false;
        List<Module> children = module.getChildren();
        boolean added = false;
        for (Module child : children) {
            DefaultMutableTreeNode cnode = new DefaultMutableTreeNode(child);
            if (addModulesToTree(cnode, child, window)) {
                added = true;
                node.add(cnode);
            }
        }
        List<Function> functions = module.getFunctions();
        for (Function function : functions) {
            if (window == null || titleMatches(window, function.getWindow())) {
                DefaultMutableTreeNode f = new DefaultMutableTreeNode(function);
                f.setAllowsChildren(false);
                node.add(f);
                added = true;
            }
        }
        return added;
    }

    private boolean titleMatches(String title, String mayBeRegex) {
        if (mayBeRegex == null)
            return false;
        if (mayBeRegex.startsWith("/") && !mayBeRegex.startsWith("//")) {
            if (!Pattern.matches(mayBeRegex.substring(1), title))
                return false;
        } else {
            if (mayBeRegex.startsWith("//"))
                mayBeRegex = mayBeRegex.substring(1);
            if (!mayBeRegex.equals(title))
                return false;
        }
        return true;
    }

    public DefaultMutableTreeNode refreshNode(String window) {
        rootNode.removeAllChildren();
        addModulesToTree(rootNode, this, window);
        return rootNode;
    }

    public boolean isFile() {
        return file;
    }

    public Module getParent() {
        return parent;
    }
}
