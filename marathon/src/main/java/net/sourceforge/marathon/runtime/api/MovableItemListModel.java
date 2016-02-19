package net.sourceforge.marathon.runtime.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;

public class MovableItemListModel extends AbstractListModel {
    private static final long serialVersionUID = 1L;
    private List<Object> dataList;

    public MovableItemListModel() {
        this(new ArrayList<Object>());
    }

    public MovableItemListModel(Object[] data) {
        this(new ArrayList<Object>(Arrays.asList(data)));
    }

    public MovableItemListModel(List<Object> list) {
        dataList = list;
    }

    public int getSize() {
        return dataList.size();
    }

    public Object getElementAt(int index) {
        return dataList.get(index);
    }

    public void add(Object data) {
        if (data != null && !dataList.contains(data))
            dataList.add(data);
        fireContentsChanged(dataList, 0, dataList.size());
    }

    public void remove(int index) {
        if (index >= dataList.size())
            return;
        dataList.remove(index);
        fireContentsChanged(dataList, 0, dataList.size());
    }

    public void moveUp(int index) {
        if (index == 0)
            return;
        Object value = dataList.get(index);
        dataList.add(index - 1, value);
        dataList.remove(index + 1);
        fireContentsChanged(dataList, 0, index);
    }

    public void moveDown(int index) {
        if (index + 1 >= dataList.size())
            return;
        Object selectedValue = dataList.get(index);
        dataList.remove(index);
        dataList.add(index + 1, selectedValue);
        fireContentsChanged(dataList, 0, index);
    }

    public void remove(Object o) {
        int index = dataList.indexOf(o);
        if (index < 0)
            return;
        remove(index);
    }
}
