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
