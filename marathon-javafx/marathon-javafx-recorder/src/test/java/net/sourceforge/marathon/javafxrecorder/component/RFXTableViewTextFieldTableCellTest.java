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
import org.testng.SkipException;
import org.testng.annotations.Test;

import ensemble.samples.controls.table.TableCellFactorySample;
import ensemble.samples.controls.table.TableCellFactorySample.EditingCell;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxrecorder.component.LoggingRecorder.Recording;

public class RFXTableViewTextFieldTableCellTest extends RFXComponentTest {

	@SuppressWarnings("unused")
	@Test
	public void select() {
		if (true) {
			throw new SkipException("Ignore the test case as it works using driver.");
		}

		TableView<?> tableView = (TableView<?>) getPrimaryStage().getScene().getRoot().lookup(".table-view");
		LoggingRecorder lr = new LoggingRecorder();
		Platform.runLater(() -> {
			Point2D point = getPoint(tableView, 1, 1);
			TableCellFactorySample.EditingCell tf = (EditingCell) getCellAt(tableView, 1, 1);
			RFXTableView rfxTableView = new RFXTableView(tableView, null, point, lr);
			rfxTableView.focusGained(null);
			tf.startEdit();
			tf.updateItem("Cell Modified", false);
			rfxTableView.focusLost(null);
		});
		List<Recording> recordings = lr.waitAndGetRecordings(1);
		Recording recording = recordings.get(0);
		AssertJUnit.assertEquals("recordSelect", recording.getCall());
		AssertJUnit.assertEquals("Cell Modified", recording.getParameters()[0]);
	}

	@Override
	protected Pane getMainPane() {
		return new TableCellFactorySample();
	}
}
