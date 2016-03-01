package net.sourceforge.marathon.javafxrecorder.component;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafxagent.Wait;

public abstract class RFXComponentTest {

	public static class ApplicationHelper extends Application {

		public static void startApplication() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Application.launch(ApplicationHelper.class);
				}
			}).start();
		}

		private Stage primaryStage;

		@Override
		public void start(Stage primaryStage) throws Exception {
			this.primaryStage = primaryStage;
			RFXComponentTest.applicationHelper = this;
		}

		public void startGUI(Pane pane) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					primaryStage.setScene(new Scene(pane));
					primaryStage.show();
				}
			});
		}

		public void hideGUI() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					primaryStage.hide();
				}
			});
		}

	}

	private static ApplicationHelper applicationHelper;

	public RFXComponentTest() {
	}

	@BeforeMethod
	public void startGUI() throws Throwable {
		if(applicationHelper == null)
			ApplicationHelper.startApplication();
		new Wait("Waiting for applicationHelper to be initialized") {
			@Override
			public boolean until() {
				return applicationHelper != null;
			}
		};
		if(applicationHelper == null) {
			throw new RuntimeException("Application Helper = null");
		}
		applicationHelper.startGUI(getMainPane());
	}

	protected abstract Pane getMainPane(); 

	@AfterMethod
	public void hideGUI() {
		applicationHelper.hideGUI();
	}

}
