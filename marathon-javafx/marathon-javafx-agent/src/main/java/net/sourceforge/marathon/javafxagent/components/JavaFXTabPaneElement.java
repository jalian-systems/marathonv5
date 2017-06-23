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
package net.sourceforge.marathon.javafxagent.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

import com.sun.javafx.scene.control.skin.TabPaneSkin;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.sourceforge.marathon.javafxagent.EventQueueWait;
import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXTargetLocator.JFXWindow;

public class JavaFXTabPaneElement extends JavaFXElement {

    public static final Logger LOGGER = Logger.getLogger(JavaFXTabPaneElement.class.getName());
    private static final Pattern CLOSE_PATTERN = Pattern.compile("(.*)::close$");

    public JavaFXTabPaneElement(Node component, IJavaFXAgent driver, JFXWindow window) {
        super(component, driver, window);
    }

    @Override public List<IJavaFXElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("nth-tab")) {
            int tabIndex = ((Integer) params[0]).intValue() - 1;
            return Arrays.asList(new JavaFXTabPaneTabJavaElement(this, tabIndex));
        } else if (selector.equals("all-tabs")) {
            int nitems = getCount();
            List<IJavaFXElement> r = new ArrayList<IJavaFXElement>();
            for (int i = 0; i < nitems; i++) {
                r.add(new JavaFXTabPaneTabJavaElement(this, i));
            }
            return r;
        }
        return super.getByPseudoElement(selector, params);
    }

    private int getCount() {
        try {
            return EventQueueWait.exec(new Callable<Integer>() {
                @Override public Integer call() {
                    TabPane pane = (TabPane) getComponent();
                    return pane.getTabs().size();
                }
            });
        } catch (Exception e) {
            throw new InternalError("Call to getTabs().size failed for TabPane#getTabs#size");
        }
    }

    @Override public boolean marathon_select(String tab) {
        Matcher matcher = CLOSE_PATTERN.matcher(tab);
        boolean isCloseTab = matcher.matches();
        tab = isCloseTab ? matcher.group(1) : tab;
        TabPane tp = (TabPane) node;
        ObservableList<Tab> tabs = tp.getTabs();
        for (int index = 0; index < tabs.size(); index++) {
            String current = getTextForTab(tp, tabs.get(index));
            if (tab.equals(current)) {
                if (isCloseTab) {
                    ((TabPaneSkin) tp.getSkin()).getBehavior().closeTab(tabs.get(index));
                    return true;
                }
                tp.getSelectionModel().select(index);
                return true;
            }
        }
        return false;
    }

    @Override public String _getText() {
        return getTextForTab((TabPane) getComponent(), ((TabPane) getComponent()).getSelectionModel().getSelectedItem());
    }

    public String getContent() {
        return new JSONArray(getContent((TabPane) getComponent())).toString();
    }
}
