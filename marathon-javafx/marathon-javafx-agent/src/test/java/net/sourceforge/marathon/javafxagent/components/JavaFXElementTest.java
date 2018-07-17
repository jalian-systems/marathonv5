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
package net.sourceforge.marathon.javafxagent.components;

import org.testng.annotations.BeforeMethod;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.sourceforge.marathon.javafxagent.Wait;

public abstract class JavaFXElementTest {

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
            JavaFXElementTest.applicationHelper = this;
        }

        public void startGUI(Pane pane) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    primaryStage.hide();
                    primaryStage.setScene(new Scene(pane));
                    primaryStage.sizeToScene();
                    primaryStage.show();
                }
            });
            new Wait("Waiting for applicationHelper to be initialized") {
                @Override
                public boolean until() {
                    try {
                        return primaryStage.getScene().getRoot() == pane;
                    } catch (Throwable t) {
                        return false;
                    }
                }
            };
        }

        public Stage getPrimaryStage() {
            return primaryStage;
        }
    }

    private static ApplicationHelper applicationHelper;

    public JavaFXElementTest() {
    }

    @BeforeMethod
    public void startGUI() throws Throwable {
        if (applicationHelper == null) {
            ApplicationHelper.startApplication();
        }
        new Wait("Waiting for applicationHelper to be initialized") {
            @Override
            public boolean until() {
                return applicationHelper != null;
            }
        };
        if (applicationHelper == null) {
            throw new RuntimeException("Application Helper = null");
        }
        applicationHelper.startGUI(getMainPane());
        new Wait() {
            @Override
            public boolean until() {
                return applicationHelper.getPrimaryStage().isShowing();
            }
        }.wait("Waiting for the primary stage to be displayed.", 10000);
    }

    protected abstract Pane getMainPane();

    public Stage getPrimaryStage() {
        return applicationHelper.getPrimaryStage();
    }

}
