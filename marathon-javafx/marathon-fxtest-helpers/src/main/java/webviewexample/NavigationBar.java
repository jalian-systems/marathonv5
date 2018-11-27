package webviewexample;
import java.io.File;
import java.net.MalformedURLException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class NavigationBar extends HBox
{
	// Create the FileChooser
	private FileChooser fileChooser = new FileChooser();

	public NavigationBar(WebView webView, String homePageUrl, boolean goToHomePage)
	{
		// Set Spacing
		this.setSpacing(4);

		// Set the Style-properties of the Navigation Bar
		this.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-insets: 5;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: blue;");

		// Create the Label
		Label label = new Label("History:");

		// Configure the FileChooser
		fileChooser.setTitle("Open Web Content");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("HTML Files", "*.html", "*.htm"));

		// Create the WebEngine
		WebEngine webEngine = webView.getEngine();

		// Create the TextField
		TextField pageUrl = new TextField();

		// Create the Buttons
		Button refreshButton = new Button("Refresh");
		Button goButton = new Button("Go");
		Button homeButton = new Button("Home");
		Button openButton = new Button("Open");

		// Let the TextField grow horizontallly
		HBox.setHgrow(pageUrl, Priority.ALWAYS);


		// Add an ActionListener to navigate to the entered URL
		pageUrl.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				webEngine.load(pageUrl.getText());
			}
		});

		// Update the stage title when a new web page title is available
		webEngine.locationProperty().addListener(new ChangeListener<String>()
		{
		    public void changed(ObservableValue<? extends String> ov,
		            final String oldvalue, final String newvalue)
		    {
		    	// Set the Title of the Stage
		    	pageUrl.setText(newvalue);
		    }
		});

		// Add an ActionListener for the Refresh Button
		refreshButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				webEngine.reload();
			}
		});

		// Add an ActionListener for the Go Button
		goButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				webEngine.load(pageUrl.getText());
			}
		});

		// Add an ActionListener for the Home Button
		homeButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				webEngine.load(homePageUrl);
			}
		});

		// Add an ActionListener for the Open Button
		openButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				File selectedFile = fileChooser.showOpenDialog(webView.getScene().getWindow());

				if (selectedFile != null)
				{
					try
					{
						webEngine.load(selectedFile.toURI().toURL().toExternalForm());
					}
					catch(MalformedURLException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});

		// Add the Children to the Navigation Bar
		this.getChildren().addAll(label, pageUrl,goButton, refreshButton, homeButton, openButton);

		if (goToHomePage)
		{
			// Load the URL
			webEngine.load(homePageUrl);
		}
	}
}
