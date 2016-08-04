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
package net.sourceforge.marathon.javafxrecorder.component;

import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafx.tests.ComboBoxTableViewSample;
import net.sourceforge.marathon.javafx.tests.ComboBoxTableViewSample.Person;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTableViewComboBoxTableCell extends RFXComponentTest {

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Test public void select() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 2, 1);
            ComboBoxTableCell cell = (ComboBoxTableCell) getCellAt(tableView, 1, 2);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            cell.startEdit();
            tableView.edit(1, (TableColumn) tableView.getColumns().get(2));
            Person person = (Person) tableView.getItems().get(1);
            person.setLastName("Jones");
            cell.commitEdit("Jones");
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Jones", recording.getParameters()[0]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" }) @Test public void selectEditable() {
        TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
        LoggingRecorder lr = new LoggingRecorder();
        Platform.runLater(() -> {
            Point2D point = getPoint(tableView, 2, 1);
            ComboBoxTableCell cell = (ComboBoxTableCell) getCellAt(tableView, 1, 2);
            cell.setEditable(true);
            RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
            rfxTableView.focusGained(null);
            cell.startEdit();
            tableView.edit(1, (TableColumn) tableView.getColumns().get(2));
            Person person = (Person) tableView.getItems().get(1);
            person.setLastName("Jones");
            cell.commitEdit("Jones");
            rfxTableView.focusLost(null);
        });
        List<Recording> recordings = lr.waitAndGetRecordings(1);
        Recording recording = recordings.get(0);
        AssertJUnit.assertEquals("recordSelect", recording.getCall());
        AssertJUnit.assertEquals("Jones", recording.getParameters()[0]);
    }

    @Override protected Pane getMainPane() {
        return new ComboBoxTableViewSample();
    }
}
