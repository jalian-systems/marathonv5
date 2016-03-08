/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
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
package com.javafx.experiments.dataapp.client.livetab;

import com.javafx.experiments.dataapp.client.DataApplication;
import com.javafx.experiments.dataapp.client.map.UnitedStatesMapPane;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

/**
 * Controller for the Live Data Tab of the car sales application
 */
public class LiveTabController implements Initializable {
    @FXML public Tab liveTab;
    @FXML public SplitPane liveView;
    @FXML public ChoiceBox regionChoiceBox;
    @FXML public ChoiceBox productChoiceBox;
    @FXML public TableView liveSaleView;
    @FXML public UnitedStatesMapPane map;
    
    private LiveDataFetcher liveDataFetcher;

    @Override public void initialize(URL url, ResourceBundle rb) {
        // set initial split pane devider position
        liveView.setDividerPosition(0, .5);
        // populate live data regions choicebox
        regionChoiceBox.setItems(DataApplication.getAmericanRegions());
        regionChoiceBox.getSelectionModel().selectFirst();
        // populate live data product types choice box
        productChoiceBox.setItems(DataApplication.getProductTypes());
        productChoiceBox.getSelectionModel().selectFirst();
        // listen for live data filter changes
        ChangeListener liveDataFilterChangedListener = new ChangeListener() {
            @Override public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                liveDataFetcher.regionOrProductChanged();
            }
        };
        regionChoiceBox.getSelectionModel().selectedItemProperty().addListener(liveDataFilterChangedListener);
        productChoiceBox.getSelectionModel().selectedItemProperty().addListener(liveDataFilterChangedListener);
        // listen for when tab pane is available and add listener for change of tabs
        liveTab.tabPaneProperty().addListener(new ChangeListener<TabPane>() {
            @Override public void changed(ObservableValue<? extends TabPane> ov, TabPane t, TabPane tabPane) {
                tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
                    @Override public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
                        if ("Live".equals(newTab.getText())) {
                            // LIVE TAB SELECTED
                            liveDataFetcher.startFetcher();
                        } else {
                            liveDataFetcher.stopFetcher();
                        }
                    }
                });
            }
        });
        // this needs to be run on FX thread 
        DataApplication.registerDataLoadingTask(new Runnable() {
            @Override public void run() {
                // create fetcher for getting live data
                liveDataFetcher = new LiveDataFetcher(liveSaleView, map, regionChoiceBox, productChoiceBox);
                // Start the live data fetcher as we start on live data tab
                liveDataFetcher.startFetcher();
            }
        });
    }
}
