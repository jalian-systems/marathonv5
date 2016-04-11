package net.sourceforge.marathon.javafxrecorder.component;

import org.json.JSONArray;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.TreeTableSample2;
import net.sourceforge.marathon.javafxagent.Wait;

public class RFXTreeTableViewTest2 extends RFXComponentTest {

    @Test public void getContent() {
        TreeTableView<?> treeTableView = (TreeTableView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-table-view");
        final Object[] content = new Object[] { null };
        Platform.runLater(() -> {
            Point2D point = getPoint(treeTableView, 1, 1);
            RFXTreeTableView rfxTreeTableView = new RFXTreeTableView(treeTableView, null, point, null);
            content[0] = rfxTreeTableView.getContent();
        });
        new Wait("Wating for contents") {
            @Override public boolean until() {
                return content[0] != null;
            }
        };
        JSONArray a = new JSONArray(content[0]);
        String expected = "[[\"Sales Department\",\"\"],[\"Ethan Williams\",\"ethan.williams@example.com\"],[\"Emma Jones\",\"emma.jones@example.com\"],[\"Michael Brown\",\"michael.brown@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"],[\"Anna Black\",\"anna.black@example.com\"],[\"Rodger York\",\"roger.york@example.com\"],[\"Susan Collins\",\"susan.collins@example.com\"]]";
        AssertJUnit.assertEquals(expected, a.toString());
    }

    @Override protected Pane getMainPane() {
        return new TreeTableSample2();
    }
}
