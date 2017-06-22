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
package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public class RFXTabPane extends RFXComponent {

    public static final Logger LOGGER = Logger.getLogger(RFXTabPane.class.getName());

    private int prevSelection = -1;

    public RFXTabPane(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
    }

    @Override protected void mousePressed(MouseEvent me) {
        Node target = (Node) me.getTarget();
        if (onCloseButton(target)) {
            recorder.recordSelect(this, getTextForTab((TabPane) node, getTab(target)) + "_close");
        }
    }

    private Tab getTab(Node target) {
        while (target != null) {
            if (target.getClass().getName().equals("com.sun.javafx.scene.control.skin.TabPaneSkin$TabHeaderSkin")) {
                try {
                    Class<? extends Node> tabHeaderSkinClass = target.getClass();
                    Method m = tabHeaderSkinClass.getDeclaredMethod("getTab");
                    m.setAccessible(true);
                    return (Tab) m.invoke(target);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            target = target.getParent();
        }
        return null;
    }

    private boolean onCloseButton(Node target) {
        return target.getStyleClass().contains("tab-close-button");
    }

    @Override protected void mouseClicked(MouseEvent me) {
        TabPane tp = (TabPane) node;
        SingleSelectionModel<Tab> selectionModel = tp.getSelectionModel();
        Tab selectedTab = selectionModel.getSelectedItem();
        if (selectedTab != null && prevSelection != selectionModel.getSelectedIndex()) {
            recorder.recordSelect(this, getTextForTab(tp, selectedTab));
        }
        prevSelection = selectionModel.getSelectedIndex();
    }

    @Override public String[][] getContent() {
        return getContent((TabPane) node);
    }

    @Override public String _getText() {
        return getTextForTab((TabPane) node, ((TabPane) node).getSelectionModel().getSelectedItem());
    }
}
