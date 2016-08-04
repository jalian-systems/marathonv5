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
package net.sourceforge.marathon.editor.html;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import net.sourceforge.marathon.editor.FileBasedEditor;
import net.sourceforge.marathon.editor.IEditor;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.ToolBarContainer;
import net.sourceforge.marathon.fxdocking.ToolBarContainer.Orientation;
import net.sourceforge.marathon.fxdocking.VLToolBar;

public class HTMLView extends FileBasedEditor implements IEditor {
    private HashMap<String, Object> dataMap = new HashMap<String, Object>();
    private WebView webView;
    private ToolBarContainer viewport;

    public HTMLView() {
        viewport = ToolBarContainer.createDefaultContainer(Orientation.RIGHT);
        webView = new WebView();
        viewport.setContent(webView);
        VLToolBar bar = new VLToolBar();
        Button openInBrowser = FXUIUtils.createButton("open-in-browser", "Open in External Browser", true);
        openInBrowser.setOnAction((event) -> {
            try {
                Desktop.getDesktop().open(fileHandler.getCurrentFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        bar.add(openInBrowser);
        viewport.getToolBarPanel().add(bar);
    }

    @Override public Object getData(String key) {
        return dataMap.get(key);
    }

    @Override public void setData(String key, Object value) {
        dataMap.put(key, value);
    }

    @Override public void setText(String text) {
        File currentFile = fileHandler.getCurrentFile();
        if (currentFile == null) {
            return;
        }
        webView.getEngine().load(currentFile.toURI().toString());
    }

    @Override public Node getNode() {
        return viewport;
    }
}
