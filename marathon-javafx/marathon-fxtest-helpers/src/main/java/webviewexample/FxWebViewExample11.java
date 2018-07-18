package webviewexample;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;

public class FxWebViewExample11 extends Application
{
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	public void start(Stage stage)
	{
		WebView webView = new WebView();
		WebEngine webEngine = webView.getEngine();
		// Load the Google web page
		webEngine.load("http://www.oracle.com");
		VBox root = new VBox(webView);
		//root.getChildrenUnmodifiable().add(webView);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
}
