package net.sourceforge.marathon.fx.projectselection;

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sourceforge.marathon.fx.api.ModalDialog;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.runtime.api.Constants;

public class ProjectSelectionTest extends AbstractModalDialogTest {

    private JavaFXAgent driver;

    @Override
    protected ModalDialog<?> getModalDialog() {
        ObservableList<ProjectInfo> projects = FXCollections.observableArrayList();
        List<List<String>> frameworks = Arrays.asList(Arrays.asList("Java/Swing Project", Constants.FRAMEWORK_SWING),
                Arrays.asList("Java/FX Project", Constants.FRAMEWORK_FX),
                Arrays.asList("Web Application Project", Constants.FRAMEWORK_WEB));
        ProjectSelection projectSelection = new ProjectSelection(projects, frameworks);
        return projectSelection;
    }

    @BeforeTest
    public void setup() {
        driver = new JavaFXAgent();
    }

    @Test
    public void test() throws Throwable {
        Thread.sleep(10000);
    }

    @Test
    public void buttonsTest() throws InterruptedException {
        List<IJavaFXElement> buttons = driver.findElementsByTagName("button");
        Assert.assertEquals(buttons.size(), 6);
        String[] actualButtons = new String[buttons.size()];
        for (int i = 0; i < buttons.size(); i++) {
            actualButtons[i] = buttons.get(i).getText();
        }
        Arrays.sort(actualButtons);
        String[] expectedButtons = { "New", "Browse", "Cancel", "Edit", "Select", "Delete" };
        Arrays.sort(expectedButtons);

        Assert.assertEquals(actualButtons, expectedButtons);

    }
}
