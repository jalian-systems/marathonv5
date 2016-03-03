package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ensemble.samples.controls.tree.TreeViewSample;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaFXAgent;
import net.sourceforge.marathon.javafxagent.Wait;

public class JavaFXTreeViewElementTest extends JavaFXElementTest {
	
	private JavaFXAgent driver;
	private IJavaElement treeView;

	@BeforeMethod
	public void initializeDriver() {
		driver = new JavaFXAgent();
		treeView = driver.findElementByTagName("tree-view");
	}

	@Test
	public void marathon_select_none() throws Throwable {
		TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
		treeView.marathon_select("[\"/Root node/Child Node 2\"]");
		new Wait("Waiting for the text item to be selected.") {
			@Override
			public boolean until() {
				return 2 == treeViewNode.getSelectionModel().getSelectedIndex();
			}
		};
		treeView.marathon_select("[]");
		new Wait("Waiting for no items selected.") {
			@Override
			public boolean until() {
				return 0 == treeViewNode.getSelectionModel().getSelectedIndices().size();
			}
		};
	}
	
	@Test
	public void marathon_select() throws Throwable {
		TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
		treeView.marathon_select("[\"/Root node/Child Node 2\"]");
		new Wait("Waiting for the text item to be selected.") {
			@Override
			public boolean until() {
				return 2 == treeViewNode.getSelectionModel().getSelectedIndex();
			}
		};
	}
	
	@Test
	public void marathon_select_multiple() throws Throwable {
		TreeView<?> treeViewNode = (TreeView<?>) getPrimaryStage().getScene().getRoot().lookup(".tree-view");
		treeViewNode.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		treeView.marathon_select("[\"/Root node/Child Node 1\",\"/Root node/Child Node 3/Child Node 7\"]");
		new Wait("Waiting for the text item to be selected.") {
			@Override
			public boolean until() {
				return 2 == treeViewNode.getSelectionModel().getSelectedIndices().size();
			}
		};
	}

	@Override
	protected Pane getMainPane() {
		return new TreeViewSample();
	}
}
