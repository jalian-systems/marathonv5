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

import com.javafx.experiments.dataapp.client.map.UnitedStatesMapPane;
import com.javafx.experiments.dataapp.client.rest.LiveSalesViewClient;
import com.javafx.experiments.dataapp.model.LiveSalesList;
import com.javafx.experiments.dataapp.model.ProductType;
import com.javafx.experiments.dataapp.model.Region;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Fetches the latest sales and fetches new sales every 6 seconds
 */
public class LiveDataFetcher extends Service<List<LiveSalesList>> {
    public static final Map<String,Color> PRODUCT_TYPE_COLOR_MAP = new HashMap<String, Color>();
    static {
        PRODUCT_TYPE_COLOR_MAP.put("Car", Color.web("#a9e200"));
        PRODUCT_TYPE_COLOR_MAP.put("SUV", Color.web("#22bad9"));
        PRODUCT_TYPE_COLOR_MAP.put("Van", Color.web("#fc8800"));
        PRODUCT_TYPE_COLOR_MAP.put("Truck", Color.web("#860061"));
        PRODUCT_TYPE_COLOR_MAP.put("Specialty", Color.web("#2f357f"));
    }
    
    private final AtomicInteger lastQuery = new AtomicInteger(-1);
    private final AtomicReference<String> regionName = new AtomicReference<String>(null);
    private final AtomicInteger productTypeID = new AtomicInteger(-1);
    private final LiveMapLocate liveMapLocate;
    private final ChoiceBox regionChoiceBox, productChoiceBox;
    private final TableView liveSales;
    private final UnitedStatesMapPane map;
    private final Group liveDots;
    private final Timeline refreshTimeline;
    
    public LiveDataFetcher(final TableView liveSales, final UnitedStatesMapPane map, 
            final ChoiceBox regionChoiceBox, final ChoiceBox productChoiceBox) {
        this.regionChoiceBox = regionChoiceBox;
        this.productChoiceBox = productChoiceBox;
        this.liveSales = liveSales;
        this.map = map;
        this.liveDots = map.getOverlayGroup();
        // get the live maps bounds
        final Bounds bounds = map.getMapBounds();
        // create liveMapLocate, that finds the pixel coordinates for a given Long and Lat value
        liveMapLocate = new LiveMapLocate(liveDots, bounds);
        // create and register listener for when new sales results come in
        stateProperty().addListener(new ChangeListener<State>() {
            @Override public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                if (newState == State.SUCCEEDED) {
                    final boolean isFreshQuery = lastQuery.get() == -1;
                    List<LiveSalesList> newL = getValue();
                    if (isFreshQuery) { // FIRST RUN
                        // clear map
                        liveDots.getChildren().clear();
                        // limit results too 100
                        if (newL.size() > 100) newL = newL.subList(0, 100);
                        // put results into table
                        liveSales.setItems(FXCollections.observableArrayList(newL));
                        // remove progress indicator
                        liveSales.setPlaceholder(null);
                        // update lastQuery ID
                        if (!newL.isEmpty()) lastQuery.set(newL.get(0).getOrderLineId());
                    } else { // APPEND NEW RESULTS
                        ObservableList<LiveSalesList> oldL = liveSales.getItems();
                        oldL.addAll(0, newL);
                        // update lastQuery ID
                        if (!newL.isEmpty()) lastQuery.set(oldL.get(0).getOrderLineId());
                    }
                    // draw results on map
                    for (LiveSalesList l : newL){
                        liveMapLocate.drawComplex(l.getLatitude(), l.getLongitude(), !isFreshQuery, 
                                PRODUCT_TYPE_COLOR_MAP.get(l.getType()));
                    }
                    // we are done reset ready for next run
                    reset();
                }
                if (getException() != null) getException().printStackTrace();
            }
        });
        // create pulling timeline execution every 6 seconds
        refreshTimeline = new Timeline();
        refreshTimeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(6000), new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    if (getState() == State.READY) {
                        updateRegionAndProductSelection();
                        // start new beackground polling
                        start();
                    } else if (getState() == State.RUNNING) {
                        System.out.println("Still running last poll, skipping ...");
                    } else {
                        System.err.println("Unexpected state reached in LiveDataFetcher: State="+getState());
                    }
                }
            })
        );
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    
    public void startFetcher() {
        System.out.println("startFetcher()");
        // run first time to get initial results
        start();
        // start polling
        refreshTimeline.playFromStart();
    }
    
    public void stopFetcher() {
        System.out.println("stopFetcher()");
        refreshTimeline.stop();
        // clear data
        liveDots.getChildren().clear();
        liveSales.getItems().clear();
        lastQuery.set(-1);
    }
    
    private boolean updateRegionAndProductSelection() {
        // get selected region
        final Object regionSelection = regionChoiceBox.getSelectionModel().getSelectedItem();
        String newRegionName = (regionSelection instanceof Region) ?
                ((Region)regionSelection).getName() : null;
        boolean regionChanged = false;
        if (regionName.get() == null) {
            regionChanged = newRegionName != null;
        } else {
            regionChanged = !regionName.get().equals(newRegionName);
        }
        if (regionChanged) {
            regionName.set(newRegionName);
            // query has changed so clear lastQuery
            lastQuery.set(-1);
            // UPDATE MAP
            if (newRegionName == null) {
                map.zoomAll();
            } else {
                map.zoomRegion(newRegionName);
            }
        }
        // get selected 
        final Object ptSelection = productChoiceBox.getSelectionModel().getSelectedItem();
        int newProductID = (ptSelection instanceof ProductType) ?
            ((ProductType)ptSelection).getProductTypeId() : -1;
        if (productTypeID.get() != newProductID) {
            productTypeID.set(newProductID);
            // query has changed so clear lastQuery
            lastQuery.set(-1);
        }
        return regionChanged;
    }
    
    public void regionOrProductChanged() {
        cancel();
        boolean regionChanged = updateRegionAndProductSelection();
        if (regionChanged) {
            // pause the data updating to wait for animation to finish
            PauseTransition delay = new PauseTransition(Duration.millis(1500));
            delay.setOnFinished(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent t) {
                    restart();
                }
            });
            delay.play();
        } else {
            restart();
        }
    }

    @Override protected Task<List<LiveSalesList>> createTask() {
        return new Task<List<LiveSalesList>>(){
            /**
             * NOTE ALL CODE IN THIS METHOD IS CALLED ON BACKGROUND THREAD
             * so must access everything in a thread safe manner
             */
            @Override protected List<LiveSalesList> call() throws Exception {
                final int lastQuery = LiveDataFetcher.this.lastQuery.get();
                final boolean isFreshQuery = lastQuery == -1;
                final String regionName = LiveDataFetcher.this.regionName.get();
                final int productTypeID = LiveDataFetcher.this.productTypeID.get();
                System.out.println("POLLING LIVE DATA >>> fresh=["+isFreshQuery+"]   region="+regionName+"   product="+productTypeID+"   lastQuery="+lastQuery);
                // fetch results
                LiveSalesViewClient liveSalesClient = new LiveSalesViewClient();
                LiveSalesList[] results = null;
                if ((regionName != null) && (productTypeID > 0)) { // REGION AND PRODUCT_TYPE SPECIFIED
                    if (isFreshQuery) {
                        results = liveSalesClient.findRecentRegionProductType_JSON(LiveSalesList[].class, regionName, productTypeID);
                    } else {
                        results = liveSalesClient.findRecentRegionProductTypeFrom_JSON(LiveSalesList[].class, regionName, productTypeID, lastQuery);
                    }
                } else if (regionName != null) { // ONLY REGION SPECIFIED
                    if (isFreshQuery) {
                        results = liveSalesClient.findRecentRegion_JSON(LiveSalesList[].class, regionName);
                    } else {
                        results = liveSalesClient.findRecentRegionFrom_JSON(LiveSalesList[].class, regionName, lastQuery);
                    }
                } else if (productTypeID > 0) { // ONLY PRODUCT_TYPE SPECIFIED
                    if (isFreshQuery) {
                        results = liveSalesClient.findRecentProductType_JSON(LiveSalesList[].class, productTypeID);
                    } else {
                        results = liveSalesClient.findRecentProductTypeFrom_JSON(LiveSalesList[].class, productTypeID, lastQuery);
                    }
                } else { // UNFILTERD, GET ALL
                    if (isFreshQuery) {
                        results = liveSalesClient.findRecent_JSON(LiveSalesList[].class);
                    } else {
                        results = liveSalesClient.findFrom_JSON(LiveSalesList[].class, lastQuery);
                    }
                }
                liveSalesClient.close();
                if (isCancelled()) results = null;
                return results == null ? null : Arrays.asList(results);
            }
        };
    }
}
