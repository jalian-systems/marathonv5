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
package net.sourceforge.marathon.fx.display;

import javafx.scene.control.TreeItem;
import net.sourceforge.marathon.display.DisplayWindow;
import net.sourceforge.marathon.editor.IEditorProvider;
import net.sourceforge.marathon.runtime.api.Module;

public class FunctionInfo {

    private String windowName;
    private Module root;
    private DisplayWindow window;

    public FunctionInfo(DisplayWindow displayWindow, String windowName, Module root) {
        this.window = displayWindow;
        this.windowName = windowName;
        this.root = root;
    }

    public String getWindowName() {
        return windowName;
    }

    public TreeItem<Object> getRoot(boolean useWindowName) {
        return root.createTreeNode(useWindowName ? windowName : null);
    }

    public TreeItem<Object> refreshNode(boolean useWindowName) {
        return root.refreshNode(useWindowName ? windowName : null);
    }

    public void refreshModuleFunctions() {
        window.refreshModuleFunctions();
    }

    public IEditorProvider getEditorProvider() {
        return window.getEditorProvider();
    }
}
