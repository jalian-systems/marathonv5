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
package net.sourceforge.marathon.runtime.api;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javafx.scene.control.TreeItem;

public class Module implements Serializable {

    public static final Logger LOGGER = Logger.getLogger(Module.class.getName());

    private static final long serialVersionUID = 1L;
    private String name;
    private List<Module> children;
    private List<Function> functionList;
    private TreeItem<Object> rootNode;
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

    @Override
    public String toString() {
        return getName();
        // return name + " (children: " + children.toString() + ") (functions: "
        // + functionList.toString() + ")";
    }

    public String getDocumentation() {
        return "";
    }

    public TreeItem<Object> createTreeNode(String window) {
        rootNode = new TreeItem<>(this);
        addModulesToTree(rootNode, this, window);
        return rootNode;
    }

    private boolean addModulesToTree(TreeItem<Object> node, Module module, String window) {
        if (module.getChildren().size() == 0 && module.getFunctions().size() == 0) {
            return false;
        }
        List<Module> children = module.getChildren();
        boolean added = false;
        for (Module child : children) {
            TreeItem<Object> cnode = new TreeItem<Object>(child);
            if (addModulesToTree(cnode, child, window)) {
                added = true;
                node.getChildren().add(cnode);
            }
        }
        List<Function> functions = module.getFunctions();
        for (Function function : functions) {
            if (window == null || titleMatches(window, function.getWindow())) {
                TreeItem<Object> f = new TreeItem<Object>(function);
                // f.setAllowsChildren(false);
                node.getChildren().add(f);
                added = true;
            }
        }
        return added;
    }

    private boolean titleMatches(String title, String mayBeRegex) {
        if (mayBeRegex == null) {
            return false;
        }
        if (mayBeRegex.startsWith("/") && !mayBeRegex.startsWith("//")) {
            if (!Pattern.matches(mayBeRegex.substring(1), title)) {
                return false;
            }
        } else {
            if (mayBeRegex.startsWith("//")) {
                mayBeRegex = mayBeRegex.substring(1);
            }
            if (!mayBeRegex.equals(title)) {
                return false;
            }
        }
        return true;
    }

    public TreeItem<Object> refreshNode(String window) {
        rootNode.getChildren().clear();
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
