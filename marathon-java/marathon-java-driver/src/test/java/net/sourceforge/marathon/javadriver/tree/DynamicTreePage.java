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
package net.sourceforge.marathon.javadriver.tree;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DynamicTreePage {

    @FindBy(css = "tree") private WebElement tree;

    @FindBy(css = "button[text='Add']") private WebElement addButton;

    @FindBy(css = "button[text='Remove']") private WebElement removeButton;

    @FindBy(css = "button[text='Clear']") private WebElement clearButton;

    public WebElement getTree() {
        return tree;
    }

    public WebElement getAddButton() {
        return addButton;
    }

    public WebElement getRemoveButton() {
        return removeButton;
    }

    public WebElement getClearButton() {
        return clearButton;
    }

}
