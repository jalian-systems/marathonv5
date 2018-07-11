/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * All Rights Reserved.
 ******************************************************************************/
package net.sourceforge.marathon;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import net.sourceforge.marathon.fx.api.FXUIUtils;

public class SimpleWebServer extends Application {

    public static final Logger LOGGER = Logger.getLogger(SimpleWebServer.class.getName());

    public static final class MyServer extends fi.iki.elonen.SimpleWebServer {
        public MyServer(String host, int port, File wwwroot, boolean quiet) {
            super(host, port, wwwroot, quiet);
        }

        @Override
        public Response serve(IHTTPSession session) {
            System.out.println(session.getMethod() + ": " + session.getUri());
            return super.serve(session);
        }

        public void setRoot(File root) {
            rootDirs.clear();
            rootDirs.add(root);
        }
    }

    public class Console extends OutputStream {
        private TextArea console;

        public Console(TextArea console) {
            this.console = console;
        }

        public void appendText(String valueOf) {
            Platform.runLater(() -> console.appendText(valueOf));
        }

        public void write(int b) throws IOException {
            appendText(String.valueOf((char) b));
        }
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private String webRoot;
    private MyServer server;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Simple Web Server");
        BorderPane root = new BorderPane();
        TextArea area = new TextArea();
        root.setCenter(area);
        ToolBar bar = new ToolBar();
        Button openInBrowser = FXUIUtils.createButton("open-in-browser", "Open in External Browser", true);
        openInBrowser.setOnAction((event) -> {
            try {
                Desktop.getDesktop().browse(URI.create(webRoot));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Button changeRoot = FXUIUtils.createButton("fldr_closed", "Change Web Root", true);
        changeRoot.setOnAction((event) -> {
            DirectoryChooser chooser = new DirectoryChooser();
            File showDialog = chooser.showDialog(primaryStage);
            if (showDialog != null)
                server.setRoot(showDialog);
        });
        bar.getItems().add(openInBrowser);
        bar.getItems().add(changeRoot);
        root.setTop(bar);
        System.setOut(new PrintStream(new Console(area)));
        System.setErr(new PrintStream(new Console(area)));
        area.setEditable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.setOnShown((e) -> startServer(getParameters().getRaw()));
        primaryStage.show();
    }

    private void startServer(List<String> argsList) {
        boolean quiet = true;
        File rootDir = new File(".");
        int port = -1;
        String host = "localhost";

        String[] args = argsList.toArray(new String[argsList.size()]);
        for (int i = 0; i < args.length; ++i) {
            if ("-h".equalsIgnoreCase(args[i]) || "--host".equalsIgnoreCase(args[i])) {
                host = args[i + 1];
            } else if ("-p".equalsIgnoreCase(args[i]) || "--port".equalsIgnoreCase(args[i])) {
                port = Integer.parseInt(args[i + 1]);
            } else if ("-v".equalsIgnoreCase(args[i]) || "--verbose".equalsIgnoreCase(args[i])) {
                quiet = false;
            }
        }
        try {
            if (port == -1) {
                port = findPort();
            }
            webRoot = "http://localhost:" + port;
            System.out.println("Starting server. Goto " + webRoot);
            server = new MyServer(host, port, rootDir, quiet);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int findPort() throws IOException {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            return socket.getLocalPort();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
