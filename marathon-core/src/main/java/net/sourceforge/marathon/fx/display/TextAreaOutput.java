package net.sourceforge.marathon.fx.display;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import net.sourceforge.marathon.display.IStdOut;
import net.sourceforge.marathon.fx.api.FXUIUtils;
import net.sourceforge.marathon.fxdocking.DockKey;
import net.sourceforge.marathon.fxdocking.DockKey.TabPolicy;
import net.sourceforge.marathon.fxdocking.Dockable;

public class TextAreaOutput extends Dockable implements IStdOut {

    public static final Logger LOGGER = Logger.getLogger(TextAreaOutput.class.getName());

    private ToolBar toolBar = new ToolBar();
    private Button clearButton = FXUIUtils.createButton("clear", "Clear Messages");
    private Button exportButton = FXUIUtils.createButton("export", "Export log to text file");
    private ToggleButton wordWrapButton = FXUIUtils.createToggleButton("wordWrap", "Toggle word wrap");
    private TextArea textArea = new TextAreaLimited();
    private BorderPane content = new BorderPane();

    private StringBuilder taText = new StringBuilder();

    private static final DockKey DOCK_KEY = new DockKey("Output", "Output", "Output from the scripts",
            FXUIUtils.getIcon("console_view"), TabPolicy.NotClosable, Side.BOTTOM);

    public TextAreaOutput() {
        initComponents();
        clear();
    }

    private void initComponents() {
        toolBar.setId("toolBar");
        toolBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        toolBar.getItems().addAll(clearButton, exportButton, wordWrapButton);
        clearButton.setDisable(true);
        exportButton.setDisable(true);
        clearButton.setOnAction((e) -> clear());
        exportButton.setOnAction(e -> onExport());
        wordWrapButton.setOnAction((e) -> textArea.setWrapText(wordWrapButton.isSelected()));
        content.setTop(toolBar);
        content.setCenter(textArea);
    }

    @Override
    public String getText() {
        return textArea.getText();
    }

    @Override
    public synchronized void append(String text, int type) {
        taText.append(text);
        if (text.contains("\n"))
            flush();
    }

    private void flush() {
        Platform.runLater(() -> {
            if (clearButton.isDisabled()) {
                clearButton.setDisable(false);
                exportButton.setDisable(false);
            }
            textArea.appendText(taText.toString());
            taText.setLength(0);
        });
    }

    @Override
    public void clear() {
        clearButton.setDisable(true);
        exportButton.setDisable(true);
        textArea.clear();
    }

    public void onExport() {
        File file = FXUIUtils.showSaveFileChooser(null, null, null, null);
        if (file != null) {
            try {
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
                stream.write(getText().getBytes());
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DockKey getDockKey() {
        return DOCK_KEY;
    }

    @Override
    public Node getComponent() {
        return content;
    }

}
