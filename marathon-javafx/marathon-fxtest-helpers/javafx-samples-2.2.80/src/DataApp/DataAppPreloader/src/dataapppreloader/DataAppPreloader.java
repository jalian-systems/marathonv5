/*
 * Copyright (c) 2008, 2011 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package dataapppreloader;

import java.util.Vector;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Preloader that creates basic application window then shows progress 
 * till the main application is loaded.
 */
public class DataAppPreloader extends Preloader  {
    private static boolean DEMO_MODE = false;
    private StackPane root;
    private StackPane background;
    private Scene preloaderScene;
    private Stage preloaderStage;
    private RaceTrack raceTrack;
    private Timeline simulatorTimeline;

    /**
     * Main method for demo/testing mode
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DEMO_MODE = true;
        launch(args);
    }

    @Override public void init() throws Exception {
        root = new StackPane();
        background = new StackPane();
        background.setId("Window");
        background.setCache(true);
        ImageView carImageView = new ImageView(new Image(
                DataAppPreloader.class.getResourceAsStream("images/car.png")));
        raceTrack = new RaceTrack();
        root.getChildren().addAll(background, raceTrack, carImageView);
        Platform.runLater(new Runnable() {
            @Override public void run() {
                preloaderScene = new Scene(root,1250,750);
                preloaderScene.getStylesheets().add(
                        DataAppPreloader.class.getResource("preloader.css").toExternalForm());
            }
        });
    }
    
    @Override public void start(Stage stage) throws Exception {
        preloaderStage = stage;
        preloaderStage.setScene(preloaderScene);
        preloaderStage.show();
        
        if (DEMO_MODE) {
            final DoubleProperty prog = new SimpleDoubleProperty(0){
                @Override protected void invalidated() {
                    handleProgressNotification(new ProgressNotification(get()));
                }
            };
            Timeline t = new Timeline();
            t.getKeyFrames().add(new KeyFrame(Duration.seconds(20), new KeyValue(prog, 1)));
            t.play();
        }
    }
    
    @Override public void handleStateChangeNotification(StateChangeNotification evt) {
        if (evt.getType() == StateChangeNotification.Type.BEFORE_INIT) {
            // check if download was crazy fast and restart progress
            if ((System.currentTimeMillis() - startDownload) < 500) {
                raceTrack.setProgress(0);
            }
            // we have finished downloading application, now we are running application
            // init() method, as we have no way of calculating real progress 
            // simplate pretend progress here
            simulatorTimeline = new Timeline();
            simulatorTimeline.getKeyFrames().add( 
                    new KeyFrame(Duration.seconds(3), 
                            new KeyValue(raceTrack.progressProperty(),1)
                    )
            );
            simulatorTimeline.play();
        }
    }

    @Override public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof PreloaderHandoverEvent) {
            // handover from preloader to application
            final PreloaderHandoverEvent event = (PreloaderHandoverEvent)info;
            final Parent appRoot = event.getRoot();
            // remove race track
            root.getChildren().remove(raceTrack);
            // stop simulating progress
            simulatorTimeline.stop();
            // apply application stylsheet to scene
            preloaderScene.getStylesheets().setAll(event.getCssUrl());
            // enable caching for smooth fade
            appRoot.setCache(true);
            // make hide appRoot then add it to scene
            appRoot.setTranslateY(preloaderScene.getHeight());
            root.getChildren().add(1,appRoot);
            // animate fade in app content
            Timeline fadeOut = new Timeline();
            fadeOut.getKeyFrames().addAll(
                new KeyFrame(
                    Duration.millis(1000), 
                    new KeyValue(appRoot.translateYProperty(), 0, Interpolator.EASE_OUT)
                ),
                new KeyFrame(
                    Duration.millis(1500), 
                    new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent t) {
                            // turn off cache as not need any more
                            appRoot.setCache(false);
                            // done animation so start loading data
                            for (Runnable task: event.getDataLoadingTasks()) {
                                Platform.runLater(task);
                            }
                        }
                    }
                )
            );
            fadeOut.play();
        }
    }
    private long startDownload = -1;

    @Override public void handleProgressNotification(ProgressNotification info) {
        if (startDownload == -1) startDownload = System.currentTimeMillis();
        raceTrack.setProgress(info.getProgress()*0.5);
    }
    
    public static class PreloaderHandoverEvent implements PreloaderNotification{
        private final Parent root;
        private final String cssUrl;
        private final Vector<Runnable> dataLoadingTasks;

        public PreloaderHandoverEvent(Parent root, String cssUrl, Vector<Runnable> dataLoadingTasks) {
            this.root = root;
            this.cssUrl = cssUrl;
            this.dataLoadingTasks = dataLoadingTasks;
        }

        public String getCssUrl() {
            return cssUrl;
        }

        public Parent getRoot() {
            return root;
        }

        public Vector<Runnable> getDataLoadingTasks() {
            return dataLoadingTasks;
        }
    }
}
