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
package net.sourceforge.marathon.javadriver.optionpane;

import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import components.DialogDemo;
import net.sourceforge.marathon.javaagent.NoSuchWindowException;
import net.sourceforge.marathon.javadriver.JavaDriver;

@Test public class JOptionPaneTest {
    private WebDriver driver;
    protected JFrame frame;
    private String parentWindow;
    private List<WebElement> optionpane;
    private WebElement tabbedPane;
    private List<WebElement> tabs;
    private List<WebElement> panels;
    private WebElement frequentPanel;
    private WebElement featurePanel;
    private WebElement iconPanel;

    @BeforeMethod public void showDialog() throws InterruptedException, InvocationTargetException, NoSuchWindowException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame = new JFrame(JOptionPaneTest.class.getSimpleName());
                frame.setName("frame-" + JOptionPaneTest.class.getSimpleName());
                frame.getContentPane().add(new DialogDemo(frame), BorderLayout.CENTER);
                frame.pack();
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });

        driver = new JavaDriver();
        parentWindow = driver.getWindowHandle();
        optionpane = driver.findElements(By.cssSelector("option-pane"));
        AssertJUnit.assertEquals(1, optionpane.size());

        tabbedPane = driver.findElement(By.cssSelector("tabbed-pane"));
        JSONArray expected = new JSONArray();
        expected.put("Simple Modal Dialogs").put("More Dialogs").put("Dialog Icons");
        AssertJUnit.assertEquals("Simple Modal Dialogs", tabbedPane.getText());

        tabs = driver.findElements(By.cssSelector("tabbed-pane::all-tabs"));
        AssertJUnit.assertEquals(3, tabs.size());
        AssertJUnit.assertEquals("Simple Modal Dialogs", tabs.get(0).getText());
        AssertJUnit.assertEquals("More Dialogs", tabs.get(1).getText());
        AssertJUnit.assertEquals("Dialog Icons", tabs.get(2).getText());

        panels = driver.findElements(By.cssSelector("tabbed-pane::all-tabs::component"));
        AssertJUnit.assertEquals(3, panels.size());
        frequentPanel = panels.get(0);
        AssertJUnit.assertEquals("panel", frequentPanel.getTagName());
        featurePanel = panels.get(1);
        AssertJUnit.assertEquals("panel", featurePanel.getTagName());
        iconPanel = panels.get(2);
        AssertJUnit.assertEquals("panel", iconPanel.getTagName());
    }

    @AfterMethod public void disposeDriver() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        if (driver != null) {
            driver.quit();
        }
    }

    public void simpleModalDialogs() throws Throwable {
        List<WebElement> tab1 = driver.findElements(By.cssSelector("tabbed-pane::nth-tab(1)"));
        AssertJUnit.assertEquals(1, tab1.size());
        AssertJUnit.assertEquals("Simple Modal Dialogs", tab1.get(0).getText());

        List<WebElement> radiobuttons = frequentPanel.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(4, radiobuttons.size());
        WebElement defaultMessageButton = radiobuttons.get(0);
        AssertJUnit.assertEquals("OK (in the L&F's words)", defaultMessageButton.getAttribute("buttonText"));
        WebElement yesNoButton = radiobuttons.get(1);
        AssertJUnit.assertEquals("Yes/No (in the L&F's words)", yesNoButton.getAttribute("buttonText"));
        WebElement yesNoInProgWordsButton = radiobuttons.get(2);
        AssertJUnit.assertEquals("Yes/No (in the programmer's words)", yesNoInProgWordsButton.getAttribute("buttonText"));
        WebElement yncButton = radiobuttons.get(3);
        AssertJUnit.assertEquals("Yes/No/Cancel (in the programmer's words)", yncButton.getAttribute("buttonText"));

        List<WebElement> buttons = frequentPanel.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(1, buttons.size());
        WebElement ShowItButton = buttons.get(0);
        AssertJUnit.assertEquals("Show it!", ShowItButton.getText());

        AssertJUnit.assertEquals("true", defaultMessageButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", yesNoButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", yesNoInProgWordsButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", yncButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", ShowItButton.getAttribute("selected"));

        List<WebElement> labels = frequentPanel.findElements(By.cssSelector("label"));
        AssertJUnit.assertEquals(1, labels.size());
        AssertJUnit.assertEquals("Some simple message dialogs:", labels.get(0).getText());

        List<WebElement> allLabels = driver.findElements(By.cssSelector("label[text*='Click the']"));
        AssertJUnit.assertEquals(1, allLabels.size());
        WebElement changePanelText = allLabels.get(0);
        AssertJUnit.assertEquals("Click the \"Show it!\" button to bring up the selected dialog.", changePanelText.getText());

        ShowItButton.click();
        driver.switchTo().window("Message");
        WebElement label;
        label = driver.findElement(By.cssSelector("label[text*='Eggs']"));
        AssertJUnit.assertEquals("Eggs aren't supposed to be green.", label.getText());
        WebElement okButton = driver.findElement(By.cssSelector("button"));
        okButton.click();

        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("Click the \"Show it!\" button to bring up the selected dialog.", changePanelText.getText());

        yesNoInProgWordsButton.click();
        AssertJUnit.assertEquals("true", yesNoInProgWordsButton.getAttribute("selected"));
        ShowItButton.click();
        driver.switchTo().window("A Silly Question");
        label = driver.findElement(By.cssSelector("label[text*='Would you']"));
        AssertJUnit.assertEquals("Would you like green eggs and ham?", label.getText());
        List<WebElement> sillyQuestnWindowButtons = driver.findElements(By.cssSelector("button"));
        WebElement yesButton = sillyQuestnWindowButtons.get(0);
        AssertJUnit.assertEquals("Yes, please", yesButton.getText());
        WebElement noButton = sillyQuestnWindowButtons.get(1);
        AssertJUnit.assertEquals("No way!", noButton.getText());
        yesButton.click();
        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("You're kidding!", changePanelText.getText());
    }

    public void moreDialogs() throws Throwable {
        WebElement tab2 = driver.findElement(By.cssSelector("tabbed-pane::nth-tab(2)"));
        AssertJUnit.assertEquals("More Dialogs", tab2.getText());
        AssertJUnit.assertTrue(tabs.get(0).isDisplayed());
        tab2.click();

        List<WebElement> radiobuttons = featurePanel.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(5, radiobuttons.size());
        WebElement pickOneButton = radiobuttons.get(0);
        AssertJUnit.assertEquals("Pick one of several choices", pickOneButton.getAttribute("buttonText"));
        WebElement textEntereButton = radiobuttons.get(1);
        AssertJUnit.assertEquals("Enter some text", textEntereButton.getAttribute("buttonText"));
        WebElement nonAutoButton = radiobuttons.get(2);
        AssertJUnit.assertEquals("Non-auto-closing dialog", nonAutoButton.getAttribute("buttonText"));
        WebElement customOptionButton = radiobuttons.get(3);
        AssertJUnit.assertEquals("Input-validating dialog (with custom message area)",
                customOptionButton.getAttribute("buttonText"));
        WebElement nonModalButton = radiobuttons.get(4);
        AssertJUnit.assertEquals("Non-modal dialog", nonModalButton.getAttribute("buttonText"));

        List<WebElement> buttons = featurePanel.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(1, buttons.size());
        WebElement ShowItButton = buttons.get(0);
        AssertJUnit.assertEquals("Show it!", ShowItButton.getText());

        pickOneButton.click();
        AssertJUnit.assertEquals("true", pickOneButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", textEntereButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", nonAutoButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", customOptionButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", nonModalButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", ShowItButton.getAttribute("selected"));

        List<WebElement> allLabels = driver.findElements(By.cssSelector("label[text*='Click the']"));
        AssertJUnit.assertEquals(1, allLabels.size());
        WebElement changePanelText = allLabels.get(0);
        AssertJUnit.assertEquals("Click the \"Show it!\" button to bring up the selected dialog.", changePanelText.getText());

        ShowItButton.click();
        driver.switchTo().window("Customized Dialog");
        WebElement label;
        label = driver.findElement(By.cssSelector("label[text*='Complete the']"));
        AssertJUnit.assertEquals("Complete the sentence:", label.getText());
        WebElement combo = driver.findElement(By.cssSelector("combo-box"));
        AssertJUnit.assertEquals("0", combo.getAttribute("selectedIndex"));
        WebElement option;
        option = driver.findElement(By.cssSelector("combo-box::nth-option(2)"));
        option.click();
        AssertJUnit.assertEquals("1", combo.getAttribute("selectedIndex"));
        WebElement okButton = driver.findElement(By.cssSelector("button[text='OK']"));
        AssertJUnit.assertEquals("OK", okButton.getText());
        WebElement cancelButton = driver.findElement(By.cssSelector("button[text='Cancel']"));
        AssertJUnit.assertEquals("Cancel", cancelButton.getText());
        okButton.click();
        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("Green eggs and... spam!", changePanelText.getText());

        textEntereButton.click();
        AssertJUnit.assertEquals("true", textEntereButton.getAttribute("selected"));
        ShowItButton.click();
        driver.switchTo().window("Customized Dialog");
        label = driver.findElement(By.cssSelector("label[text*='Green eggs']"));
        AssertJUnit.assertEquals("\"Green eggs and...\"", label.getText());
        WebElement textField = driver.findElement(By.cssSelector("multiplexing-text-field"));
        textField.sendKeys("spam", Keys.TAB, Keys.ENTER);
        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("Green eggs and... spam!", changePanelText.getText());

        customOptionButton.click();
        AssertJUnit.assertEquals("true", customOptionButton.getAttribute("selected"));
        ShowItButton.click();
        driver.switchTo().window("Quiz");
        WebElement enterLastName = driver.findElement(By.cssSelector("text-field"));
        enterLastName.sendKeys("James");
        List<WebElement> quizDialogButtons = driver.findElements(By.cssSelector("button"));
        WebElement enterButton = quizDialogButtons.get(0);
        AssertJUnit.assertEquals("Enter", enterButton.getText());
        WebElement cancelButton2 = quizDialogButtons.get(1);
        AssertJUnit.assertEquals("Cancel", cancelButton2.getText());
        enterButton.click();
        driver.switchTo().window("Try again");
        label = driver.findElement(By.cssSelector("label[text*='James']"));
        AssertJUnit.assertEquals("Sorry, \"James\" isn't a valid response.", label.getText());
        WebElement okButton2 = driver.findElement(By.cssSelector("button"));
        AssertJUnit.assertEquals("OK", okButton2.getText());
        okButton2.click();
        driver.switchTo().window("Quiz");
        enterLastName.sendKeys("GEISEL", Keys.TAB, Keys.ENTER);
        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("Congratulations!  You entered \"GEISEL\".", changePanelText.getText());

        nonModalButton.click();
        AssertJUnit.assertEquals("true", nonModalButton.getAttribute("selected"));
        ShowItButton.click();
        driver.switchTo().window("A Non-Modal Dialog");
        label = driver.findElement(By.cssSelector("label"));
        AssertJUnit.assertEquals("<html><p align=center>This is a non-modal dialog."
                + "<br>You can have one or more of these up<br>and still use the main window.", label.getText());
        driver.switchTo().window(parentWindow);
        driver.manage().window().setPosition(new Point(200, 200));
        ShowItButton.click();
        Set<String> pops = driver.getWindowHandles();
        AssertJUnit.assertEquals(3, pops.size());
    }

    public void dialogIcons() throws Throwable {
        WebElement tab3 = driver.findElement(By.cssSelector("tabbed-pane::nth-tab(3)"));
        AssertJUnit.assertEquals("Dialog Icons", tab3.getText());
        AssertJUnit.assertTrue(tabs.get(0).isDisplayed());
        tab3.click();

        List<WebElement> radiobuttons = iconPanel.findElements(By.cssSelector("radio-button"));
        AssertJUnit.assertEquals(6, radiobuttons.size());
        WebElement plainButton = radiobuttons.get(0);
        AssertJUnit.assertEquals("Plain (no icon)", plainButton.getAttribute("buttonText"));
        WebElement infoButton = radiobuttons.get(2);
        AssertJUnit.assertEquals("Information icon", infoButton.getAttribute("buttonText"));
        WebElement questionButton = radiobuttons.get(4);
        AssertJUnit.assertEquals("Question icon", questionButton.getAttribute("buttonText"));
        WebElement errorButton = radiobuttons.get(1);
        AssertJUnit.assertEquals("Error icon", errorButton.getAttribute("buttonText"));
        WebElement warningButton = radiobuttons.get(3);
        AssertJUnit.assertEquals("Warning icon", warningButton.getAttribute("buttonText"));
        WebElement customButton = radiobuttons.get(5);
        AssertJUnit.assertEquals("Custom icon", customButton.getAttribute("buttonText"));

        List<WebElement> buttons = iconPanel.findElements(By.cssSelector("button"));
        AssertJUnit.assertEquals(1, buttons.size());
        WebElement ShowItButton = buttons.get(0);
        AssertJUnit.assertEquals("Show it!", ShowItButton.getText());

        plainButton.click();
        AssertJUnit.assertEquals("true", plainButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", infoButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", questionButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", errorButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", warningButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", customButton.getAttribute("selected"));
        AssertJUnit.assertEquals("false", ShowItButton.getAttribute("selected"));

        List<WebElement> allLabels = driver.findElements(By.cssSelector("label[text*='Click the']"));
        AssertJUnit.assertEquals(1, allLabels.size());
        WebElement changePanelText = allLabels.get(0);
        AssertJUnit.assertEquals("Click the \"Show it!\" button to bring up the selected dialog.", changePanelText.getText());

        warningButton.click();
        AssertJUnit.assertEquals("true", warningButton.getAttribute("selected"));
        ShowItButton.click();
        driver.switchTo().window("Inane warning");
        WebElement label;
        label = driver.findElement(By.cssSelector("label[text^='Eggs']"));
        AssertJUnit.assertEquals("Eggs aren't supposed to be green.", label.getText());
        WebElement okButton = driver.findElement(By.cssSelector("button"));
        okButton.click();
        driver.switchTo().window(parentWindow);
        AssertJUnit.assertEquals("Click the \"Show it!\" button to bring up the selected dialog.", changePanelText.getText());

    }

}
