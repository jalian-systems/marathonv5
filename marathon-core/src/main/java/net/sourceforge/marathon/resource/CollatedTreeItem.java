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
package net.sourceforge.marathon.resource;

import java.util.Comparator;
import java.util.function.Predicate;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TreeItem;

public class CollatedTreeItem<T> extends TreeItem<T> {

    private Predicate<T> filter = t -> true;
    private ObservableList<TreeItem<T>> children;
    private FilteredList<TreeItem<T>> filteredChildren;
    private SortedList<TreeItem<T>> sortedChildren;
    private Comparator<T> comparator;

    public CollatedTreeItem() {
        children = FXCollections.observableArrayList();
        filteredChildren = new FilteredList<>(children, new Predicate<TreeItem<T>>() {
            @Override public boolean test(TreeItem<T> t) {
                return filter.test(t.getValue());
            }
        });
        sortedChildren = new SortedList<>(filteredChildren);
        ObservableList<TreeItem<T>> original = super.getChildren();
        sortedChildren.addListener(new ListChangeListener<TreeItem<T>>() {
            @Override public void onChanged(javafx.collections.ListChangeListener.Change<? extends TreeItem<T>> c) {
                while (c.next()) {
                    original.removeAll(c.getRemoved());
                    original.addAll(c.getFrom(), c.getAddedSubList());
                }
            }
        });
    }

    public void setPredicate(Predicate<T> p) {
        this.filter = p;
        setCollation();
    }

    public void setCollation() {
        if (comparator == null) {
            sortedChildren.setComparator(null);
        } else {
            sortedChildren.setComparator((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));
        }
        if (filter == null) {
            filteredChildren.setPredicate(null);
        } else {
            filteredChildren.setPredicate(new Predicate<TreeItem<T>>() {
                @Override public boolean test(TreeItem<T> t) {
                    return filter.test(t.getValue());
                }
            });
        }
    }

    public void setComparator(Comparator<T> c) {
        this.comparator = c;
        setCollation();
    }
}
