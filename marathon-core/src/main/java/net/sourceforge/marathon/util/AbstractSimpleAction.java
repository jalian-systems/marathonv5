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
package net.sourceforge.marathon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import net.sourceforge.marathon.fx.api.FXUIUtils;

/**
 * this is a simple action class that allows you to specify stuff we care about
 * in the constructor each instance will have to subclass it to provide an
 * actionPeformed method as well
 */
public abstract class AbstractSimpleAction extends ActionEvent implements EventHandler<ActionEvent> {

    public static final Logger LOGGER = Logger.getLogger(AbstractSimpleAction.class.getName());

    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String mneumonic;

    private List<Button> buttons = new ArrayList<>();

    private List<MenuItem> menuItems = new ArrayList<>();

    private String commandName;

    public AbstractSimpleAction(String name, String description, String mneumonic, String commandName) {
        this.name = name;
        this.description = description;
        this.mneumonic = mneumonic;
        this.commandName = commandName;
    }

    @Override
    public String toString() {
        return "AbstractSimpleAction [name=" + name + ", description=" + description + ", mneumonic=" + mneumonic + ", commandName="
                + commandName + "]";
    }

    public MenuItem getMenuItem() {
        String cmd = commandName;
        if (cmd == null) {
            cmd = expand(name);
        }
        MenuItem menuItem = FXUIUtils.createMenuItem(name, cmd, mneumonic);
        menuItem.setOnAction(this);
        menuItems.add(menuItem);
        return menuItem;
    }

    private String expand(String fieldName) {
        StringBuffer sb = new StringBuffer();
        char[] charArray = fieldName.substring(1).toCharArray();
        sb.append(Character.toUpperCase(fieldName.charAt(0)));
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                sb.append(' ');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public Button getButton() {
        Button button = FXUIUtils.createButton(name, description);
        button.setOnAction(this);
        buttons.add(button);
        return button;
    }

    public Button getButtonWithText() {
        String cmd = commandName;
        if (cmd == null) {
            cmd = expand(name);
        }
        Button button = FXUIUtils.createButton(name, description, true, cmd);
        button.setOnAction(this);
        buttons.add(button);
        return button;
    }

    public void setEnabled(boolean b) {
        for (Button button : buttons) {
            button.setDisable(!b);
        }
        for (MenuItem menuItem : menuItems) {
            menuItem.setDisable(!b);
        }
    }

    @Override
    public abstract void handle(ActionEvent e);

    public String getName() {
        return name;
    }

    public Node getIcon() {
        return FXUIUtils.getIcon(name);
    }

    public ToggleButton getToggleButton() {
        ToggleButton toggleButton = FXUIUtils.createToggleButton(name, description);
        toggleButton.setOnAction(this);
        return toggleButton;
    }

}
