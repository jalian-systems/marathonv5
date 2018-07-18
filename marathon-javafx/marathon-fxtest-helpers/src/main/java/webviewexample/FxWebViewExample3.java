package webviewexample;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class FxWebViewExample3 extends Application
{
	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(final Stage stage)
	{
		// Create the WebView
		WebView webView = new WebView();

		// Update the stage title when a new web page title is available
		webView.getEngine().titleProperty().addListener(new ChangeListener<String>()
		{
		    public void changed(ObservableValue<? extends String> ov,
		            final String oldvalue, final String newvalue)
		    {
		    	// Set the Title of the Stage
		    	stage.setTitle(newvalue);
		    }
		});

		// Load the Google web page
		String homePageUrl = "http://www.google.com";

		// Create the WebMenu
		MenuButton menu = new WebMenu(webView);

		// Create the Navigation Bar
		NavigationBar navigationBar = new NavigationBar(webView, homePageUrl, true);
		// Add the children to the Navigation Bar
		navigationBar.getChildren().add(menu);

		// Create the VBox
		VBox root = new VBox(navigationBar, webView);

		// Set the Style-properties of the VBox
		root.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-insets: 5;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: blue;");

		// Create the Scene
		Scene scene = new Scene(root);
		// Add the Scene to the Stage
		stage.setScene(scene);
		// Display the Stage
		stage.show();
	}
}
