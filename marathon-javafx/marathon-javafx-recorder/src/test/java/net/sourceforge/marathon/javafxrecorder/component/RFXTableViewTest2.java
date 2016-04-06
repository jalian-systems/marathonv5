package net.sourceforge.marathon.javafxrecorder.component;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TableSample1;
import net.sourceforge.marathon.javafxagent.Wait;

public class RFXTableViewTest2 extends RFXComponentTest {

    @Test public void getContent() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 1, 1);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, null);
            content[0] = rfxTableView.getContent();
        });
        new Wait("Wating for contents") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Jacob\",\"Smith\",\"jacob.smith@example.com\"],[\"Isabella\",\"Johnson\",\"isabella.johnson@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"],[\"Ethan\",\"Williams\",\"ethan.williams@example.com\"],[\"Emma\",\"Jones\",\"emma.jones@example.com\"],[\"Michael\",\"Brown\",\"michael.brown@example.com\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @Override protected Pane getMainPane() {
        return new TableSample1();
    }
}
