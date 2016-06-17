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
package net.sourceforge.marathon.runtime.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;

public class MovableItemListModel extends AbstractListModel<Object> {
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
