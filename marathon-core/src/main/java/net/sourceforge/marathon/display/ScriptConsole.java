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
package net.sourceforge.marathon.display;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.sourceforge.marathon.display.readline.TextAreaReadline;
import net.sourceforge.marathon.fx.display.TextAreaLimited;
import net.sourceforge.marathon.runtime.api.Constants;

public class ScriptConsole extends Stage implements IStdOut {

    public static final Logger LOGGER = Logger.getLogger(ScriptConsole.class.getName());

    private TextField textField;
    private BorderPane root = new BorderPane();
    private TextAreaReadline textAreaReadline;
    protected PrintWriter spooler;
    private PrintStream oldOut;
    private PrintStream oldErr;
    private List<TextNodeInfo> textNodes = new ArrayList<>();
    private TextArea textArea = new TextAreaLimited();

    public ScriptConsole(IScriptConsoleListener l, String spoolSuffix) {
        setTitle("Script Console");
        initModality(Modality.APPLICATION_MODAL);
        textField = new TextField();
        root.setCenter(textArea);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Text promptText = new Text(">>");
        promptText.setFont(Font.font("Verdana", FontWeight.MEDIUM, 12));
        promptText.setFill(Color.rgb(0xa4, 0x00, 0x00));
        TextFlow textFlow = new TextFlow(promptText);
        textFlow.setStyle("-fx-background-color: white;");
        HBox.setMargin(textFlow, new Insets(5, 0, 0, 0));
        root.setBottom(new HBox(textFlow, textField));
        setScene(new Scene(root, 640, 480));
        textAreaReadline = new TextAreaReadline(textField, textArea, "Marathon Script Console \n\n") {
            @Override public void handle(KeyEvent event) {
                if (event.getEventType() == KeyEvent.KEY_PRESSED && event.getCode() == KeyCode.ESCAPE) {
                    textAreaReadline.shutdown();
                    if (spooler != null) {
                        spooler.close();
                    }
                    resetStdStreams();
                } else {
                    super.handle(event);
                }
            }
        };
        final String projectDir = System.getProperty(Constants.PROP_PROJECT_DIR);
        try {
            textAreaReadline.setHistoryFile(new File(projectDir, ".history"));
        } catch (IOException e1) {
        }
        setOnCloseRequest((we) -> {
            if (we.getEventType() == WindowEvent.WINDOW_CLOSE_REQUEST) {
                textAreaReadline.shutdown();
                if (spooler != null) {
                    spooler.close();
                }
                resetStdStreams();
            }
        });
        try {
            spooler = new PrintWriter(new FileWriter(new File(projectDir, "spool" + spoolSuffix), true));
        } catch (IOException e1) {
        }
        Thread t2 = new Thread() {
            @Override public void run() {
                String line = null;
                while ((line = textAreaReadline.readLine(">> ")) != null) {
                    line = line.trim();
                    if (line.equals("")) {
                        continue;
                    }
                    spooler.println(line);
                    spooler.flush();
                    if (line.equals("help")) {
                        line = "marathon_help()";
                    } else if (line.equals("spool clear")) {
                        try {
                            if (spooler != null) {
                                spooler.close();
                            }
                            spooler = new PrintWriter(new FileWriter(new File(projectDir, "spool" + spoolSuffix), false));
                        } catch (IOException e) {
                        }
                        continue;
                    }
                    if (!line.contains("\n")) {
                        textAreaReadline.getHistory().addToHistory(line);
                    }
                    String ret = l.evaluateScript(line);
                    if (ret != null && !ret.equals("")) {
                        append("=> " + ret + "\n", IStdOut.STD_OUT);
                    }
                }
                l.sessionClosed();
            }

        };
        t2.start();
    }

    @Override public String getText() {
        return textField.getText();
    }

    @Override public void append(String text, int type) {
        addInfo(text, type);
        if (text.contains("\n")) {
            List<TextNodeInfo> writeNow = textNodes;
            textNodes = new ArrayList<>();
            Platform.runLater(() -> {
                for (TextNodeInfo textNodeInfo : writeNow) {
                    OutputStream stream = null;
                    try {
                        stream = isErrorType(textNodeInfo.getType()) ? textAreaReadline.getErrorStream()
                                : textAreaReadline.getOutputStream();
                        stream.write(textNodeInfo.getText().toString().getBytes());
                        stream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (stream != null) {
                            try {
                                stream.close();
                                if (!textField.isEditable())
                                    textField.setEditable(true);
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            });
        }
    }

    private void addInfo(String text, int type) {
        if (textNodes.size() == 0) {
            textNodes.add(new TextNodeInfo(text, type));
        } else {
            TextNodeInfo lastNode = textNodes.get(textNodes.size() - 1);
            if (lastNode.getType() == type) {
                lastNode.getText().append(text);
            } else {
                textNodes.add(new TextNodeInfo(text, type));
            }
        }
    }

    private boolean isErrorType(int type) {
        return type == IStdOut.SCRIPT_ERR || type == IStdOut.STD_ERR;
    }

    @Override public void clear() {
        textField.setText("");
    }

    private void resetStdStreams() {
        System.setOut(oldOut);
        System.setErr(oldErr);
    }

    public void dispose() {
        Platform.runLater(() -> super.close());
    }

    public void setVisible(boolean b) {
        if (b) {
            setStdStreams();
            super.show();
        }
    }

    private void setStdStreams() {
        oldOut = System.out;
        oldErr = System.err;
        oldOut.flush();
        oldErr.flush();
        System.setErr(new PrintStream(new OutputStream() {
            @Override public void write(int b) throws IOException {
                append((byte) b, IStdOut.STD_ERR);
            }
        }));
    }

    public void append(byte b, int type) {
        OutputStream stream = null;
        try {
            stream = isErrorType(type) ? textAreaReadline.getErrorStream() : textAreaReadline.getOutputStream();
            stream.write(new byte[] { b });
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                    if (!textField.isEditable())
                        textField.setEditable(true);
                } catch (IOException e) {
                }
            }
        }
    }

    private static class TextNodeInfo {
        private StringBuffer text = new StringBuffer();
        private int type;

        public TextNodeInfo(String text, int type) {
            this.text.append(text);
            this.type = type;
        }

        public StringBuffer getText() {
            return text;
        }

        public int getType() {
            return type;
        }
    }

    public static class Main extends Application {

        @Override public void start(Stage primaryStage) throws Exception {
            ScriptConsole scriptConsole = new ScriptConsole(null, null);
            scriptConsole.show();
        }

        public static void main(String[] args) {
            launch(args);
        }

    }

}
