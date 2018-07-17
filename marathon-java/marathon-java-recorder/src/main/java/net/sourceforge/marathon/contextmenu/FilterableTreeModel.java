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
package net.sourceforge.marathon.contextmenu;

import java.util.logging.Logger;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FilterableTreeModel<T> implements TreeModel {

    public static final Logger LOGGER = Logger.getLogger(FilterableTreeModel.Predicate.class.getName());

    public static interface Predicate<T> {
        public boolean apply(T o);
    }

    private TreeModel treeModel;
    private Predicate<T> predicate;

    public FilterableTreeModel(TreeModel defaultTreeModel, Predicate<T> predicate) {
        this.treeModel = defaultTreeModel;
        this.predicate = predicate;
    }

    public FilterableTreeModel(TreeModel defaultTreeModel) {
        this.treeModel = defaultTreeModel;
        this.predicate = new Predicate<T>() {
            @Override
            public boolean apply(T o) {
                return true;
            }
        };
    }

    @Override
    public Object getRoot() {
        return treeModel.getRoot();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getIndexOfChild(Object parent, Object child2) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child)) {
                if (child == child2) {
                    return ourCount;
                }
                ourCount++;
            }
        }
        return -1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getChild(Object parent, int index) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child)) {
                if (ourCount == index) {
                    return child;
                }
                ourCount++;
            }
        }
        throw new RuntimeException("Reached unexpected code point");
    }

    @Override
    @SuppressWarnings("unchecked")
    public int getChildCount(Object parent) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child)) {
                ourCount++;
            }
        }
        return ourCount;
    }

    @Override
    public boolean isLeaf(Object node) {
        return treeModel.isLeaf(node);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        treeModel.addTreeModelListener(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        treeModel.removeTreeModelListener(l);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        treeModel.valueForPathChanged(path, newValue);
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }
}
