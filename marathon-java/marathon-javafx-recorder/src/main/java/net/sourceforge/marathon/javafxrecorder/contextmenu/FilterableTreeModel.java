package net.sourceforge.marathon.javafxrecorder.contextmenu;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class FilterableTreeModel<T> implements TreeModel {

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
            @Override public boolean apply(T o) {
                return true;
            }
        };
    }

    public Object getRoot() {
        return treeModel.getRoot();
    }

    @SuppressWarnings("unchecked") public int getIndexOfChild(Object parent, Object child2) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child)) {
                if (child == child2)
                    return ourCount;
                ourCount++;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked") public Object getChild(Object parent, int index) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child)) {
                if (ourCount == index)
                    return child;
                ourCount++;
            }
        }
        throw new RuntimeException("Reached unexpected code point");
    }

    @SuppressWarnings("unchecked") public int getChildCount(Object parent) {
        int childCount = treeModel.getChildCount(parent);
        int ourCount = 0;
        for (int i = 0; i < childCount; i++) {
            Object child = treeModel.getChild(parent, i);
            if (predicate.apply((T) child))
                ourCount++;
        }
        return ourCount;
    }

    public boolean isLeaf(Object node) {
        return treeModel.isLeaf(node);
    }

    public void addTreeModelListener(TreeModelListener l) {
        treeModel.addTreeModelListener(l);
    }

    public void removeTreeModelListener(TreeModelListener l) {
        treeModel.removeTreeModelListener(l);
    }

    @Override public void valueForPathChanged(TreePath path, Object newValue) {
        treeModel.valueForPathChanged(path, newValue);
    }

    public void setPredicate(Predicate<T> predicate) {
        this.predicate = predicate;
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }
}