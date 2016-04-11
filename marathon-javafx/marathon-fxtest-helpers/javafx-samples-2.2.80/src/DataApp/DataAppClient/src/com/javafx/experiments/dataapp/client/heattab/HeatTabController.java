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
package com.javafx.experiments.dataapp.client.heattab;

import com.javafx.experiments.dataapp.client.DataApplication;
import com.javafx.experiments.dataapp.client.map.UnitedStatesMapPane;
import com.javafx.experiments.dataapp.client.rest.HeatMapClient;
import com.javafx.experiments.dataapp.model.ProductType;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.transit.HeatMapQuantity;
import com.javafx.experiments.dataapp.model.transit.HeatMapRange;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 *
 */
public class HeatTabController implements Initializable {
    @FXML public UnitedStatesMapPane map;
    @FXML public ChoiceBox<Month> compareMonthChoiceBox;
    @FXML public ChoiceBox<Month> toMonthChoiceBox;
    @FXML public ChoiceBox compareProductChoiceBox;
    @FXML public ChoiceBox toProductChoiceBox;
    @FXML public ChoiceBox regionChoiceBox;
    
    private static final Stop[] COLD_COLORS = new Stop[]{
        new Stop(0,     Color.WHITE),
        new Stop(0.33,  Color.web("#63caff")),
        new Stop(0.66,  Color.web("#4c73c2")),
        new Stop(1,     Color.web("#1a2644"))
    };
    private static final Stop[] HOT_COLORS = new Stop[]{
        new Stop(0,     Color.WHITE),
        new Stop(0.33,  Color.web("#ffae00")),
        new Stop(0.66,  Color.web("#ff0000")),
        new Stop(1,     Color.web("#700000"))
    };
    private HeatMapClient hmc;
    private Map<String,Label> stateLabelMap = new HashMap<String,Label>();

    @Override public void initialize(URL url, ResourceBundle rb) {
        // populate live data regions choicebox
        regionChoiceBox.setItems(DataApplication.getAmericanRegions());
        regionChoiceBox.getSelectionModel().selectFirst();
        regionChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override public void changed(ObservableValue ov, Object t, Object newValue) {
                if (newValue instanceof Region) {
                    Region region = (Region)newValue;
                    Timeline fadeLabels = new Timeline();
                    for (Node label: map.getOverlayGroup().getChildren()) {
                        if (label.isVisible()) {
                            final Node finalLabel = label;
                            fadeLabels.getKeyFrames().addAll(
                                new KeyFrame(Duration.millis(0),
                                    new KeyValue(finalLabel.opacityProperty(), 1)
                                ),
                                new KeyFrame(Duration.millis(500),
                                    new EventHandler<ActionEvent>() {
                                        @Override public void handle(ActionEvent t) {
                                            finalLabel.setVisible(false);
                                            finalLabel.setCache(false);
                                        }
                                    },
                                    new KeyValue(finalLabel.opacityProperty(), 0, Interpolator.EASE_OUT)
                                )
                            );
                        }
                    }
                    for (String state: region.computeStates()) {
                        final Label stateLabel = stateLabelMap.get(state);
                        if (stateLabel != null) {
                            stateLabel.setOpacity(0);
                            stateLabel.setVisible(true);
                            stateLabel.setCache(true);
                            fadeLabels.getKeyFrames().addAll(
                                new KeyFrame(Duration.millis(2000),
                                    new KeyValue(stateLabel.opacityProperty(), 0)
                                ),
                                new KeyFrame(Duration.millis(2500),
                                    new KeyValue(stateLabel.opacityProperty(), 1, Interpolator.EASE_IN)
                                )
                            );
                        }
                    }
                    map.zoomRegion(region.getName());
                    fadeLabels.play();
                } else {
                    Timeline fadeLabels = new Timeline();
                    for (Node label: map.getOverlayGroup().getChildren()) {
                        if (label.isVisible()) {
                            final Node finalLabel = label;
                            fadeLabels.getKeyFrames().addAll(
                                new KeyFrame(Duration.millis(1000),
                                    new KeyValue(finalLabel.opacityProperty(), 1)
                                ),
                                new KeyFrame(Duration.millis(1500),
                                    new EventHandler<ActionEvent>() {
                                        @Override public void handle(ActionEvent t) {
                                            finalLabel.setVisible(false);
                                        }
                                    },
                                    new KeyValue(finalLabel.opacityProperty(), 0, Interpolator.EASE_OUT)
                                )
                            );
                        }
                    }
                    fadeLabels.play();
                    map.zoomAll();
                }
            }
        });
        // populate product types choice box
        compareProductChoiceBox.setItems(DataApplication.getProductTypes());
        compareProductChoiceBox.getSelectionModel().selectFirst();
        toProductChoiceBox.setItems(DataApplication.getProductTypes());
        toProductChoiceBox.getSelectionModel().selectFirst();
        // listen for selection changes
        final ChangeListener rangeChangeListener = new ChangeListener() {
            @Override public void changed(ObservableValue ov, Object t, Object t1) {
                fetchResults();
            }
        };
        compareProductChoiceBox.getSelectionModel().selectedItemProperty().addListener(rangeChangeListener);
        toProductChoiceBox.getSelectionModel().selectedItemProperty().addListener(rangeChangeListener);
        
        createStateLabels();
        
        // this needs to be run on FX thread 
        DataApplication.registerDataLoadingTask(new Runnable() {
            @Override public void run() {
                // create task to fetch range of available dates in background
                Task<HeatMapRange> getHeatMapRangeTask =  new Task<HeatMapRange>(){
                    @Override protected HeatMapRange call() throws Exception {
                        hmc = new HeatMapClient();
                        return hmc.getDateRange_JSON(HeatMapRange.class); 
                    }
                };
                // listen for results and then update ui, and fetch initial data
                getHeatMapRangeTask.valueProperty().addListener(new ChangeListener<HeatMapRange>() {
                    @Override public void changed(ObservableValue<? extends HeatMapRange> ov, HeatMapRange t, HeatMapRange hmr) {
                        // create list of months
                        List<Month> months = new ArrayList<Month>();
                        Calendar endCal = Calendar.getInstance();
                        endCal.setTime(hmr.getMaxDate());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(hmr.getMinDate());
                        cal.set(Calendar.DAY_OF_MONTH,0);
                        cal.set(Calendar.HOUR_OF_DAY,0);
                        cal.set(Calendar.MINUTE,0);
                        cal.set(Calendar.SECOND,0);
                        cal.set(Calendar.MILLISECOND,0);
                        months.add(new Month(cal.getTime()));
                        while (cal.before(endCal)) {
                            cal.add(Calendar.MONTH,1);
                            months.add(new Month(cal.getTime()));
                        }
                        compareMonthChoiceBox.getItems().setAll(months);
                        compareMonthChoiceBox.getSelectionModel().select(months.size()-1);
                        toMonthChoiceBox.getItems().setAll(months);
                        toMonthChoiceBox.getSelectionModel().select(months.size()-13);
                        // add change listeners to month choice boxes
                        compareMonthChoiceBox.getSelectionModel().selectedItemProperty().addListener(rangeChangeListener);
                        toMonthChoiceBox.getSelectionModel().selectedItemProperty().addListener(rangeChangeListener);
                        // fetch initial results
                        fetchResults();
                    }
                });
                // start background task
                new Thread(getHeatMapRangeTask).start();
            }
        });
    }
    
    private void createStateLabels() {
        Group overlay = map.getOverlayGroup();
        for(String state: Region.ALL_STATES) {
            Node stateNode = map.lookup("#"+state);
            if (stateNode != null) {
                Label label = new Label("+10");
                label.getStyleClass().add("heatmap-label");
                label.setTextAlignment(TextAlignment.CENTER);
                label.setAlignment(Pos.CENTER);
                label.setManaged(false);
                label.setOpacity(0);
                label.setVisible(false);
                Bounds stateBounds = stateNode.getBoundsInParent();
                if ("DE".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX()-25, stateBounds.getMinY(), 
                            stateBounds.getWidth()+50, stateBounds.getHeight());
                } else if ("VT".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX(), stateBounds.getMinY()-25, 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("NH".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX(), stateBounds.getMinY()+30, 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("MA".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX()-20, stateBounds.getMinY()-18, 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("RI".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX(), stateBounds.getMinY(), 
                            stateBounds.getWidth()+40, stateBounds.getHeight());
                } else if ("ID".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX(), stateBounds.getMinY()+60, 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("MI".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX()+60, stateBounds.getMinY(), 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("FL".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX()+95, stateBounds.getMinY(), 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else if ("LA".equals(state)) {
                    label.resizeRelocate(stateBounds.getMinX()-50, stateBounds.getMinY(), 
                            stateBounds.getWidth(), stateBounds.getHeight());
                } else {
                    label.resizeRelocate(stateBounds.getMinX(), stateBounds.getMinY(), 
                            stateBounds.getWidth(), stateBounds.getHeight());
                }
                stateLabelMap.put(state, label);
                overlay.getChildren().add(label);
            }
        }
    }
    
    private void fetchResults() {
        System.err.println("fetchResults() ");
        System.err.println("    compareMonthChoiceBox = " + compareMonthChoiceBox);
        System.err.println("    compareMonthChoiceBox.getSelectionModel().getSelectedItem() = " + compareMonthChoiceBox.getSelectionModel().getSelectedItem());
        System.err.println("    toMonthChoiceBox = " + toMonthChoiceBox);
        System.err.println("    compareProductChoiceBox = " + compareProductChoiceBox);
        System.err.println("    toProductChoiceBox = " + toProductChoiceBox);
        fetchResults(
                compareMonthChoiceBox.getSelectionModel().getSelectedItem().date, 
                toMonthChoiceBox.getSelectionModel().getSelectedItem().date,
                compareProductChoiceBox.getSelectionModel().getSelectedItem() instanceof ProductType ? 
                        ((ProductType)compareProductChoiceBox.getSelectionModel().getSelectedItem()).getProductTypeId() : -1,
                toProductChoiceBox.getSelectionModel().getSelectedItem() instanceof ProductType ? 
                        ((ProductType)toProductChoiceBox.getSelectionModel().getSelectedItem()).getProductTypeId() : -1
        );
    }
    
    private void fetchResults(final Date compareMonth, final Date toMonth, 
                final int compareProductID, final int toProductID) {
        System.out.println("fetchResults  "+compareMonth+",  "+toMonth+"  -- "+compareProductID+" , "+toProductID);
        Task<HeatMapQuantity[]> getHeatMapDataTask =  new Task<HeatMapQuantity[]>(){
            @Override protected HeatMapQuantity[] call() throws Exception {
                return hmc.getProductTypeHeatMap_JSON(HeatMapQuantity[].class, compareMonth, toMonth, compareProductID, toProductID);
            }
        };
        // listen for results and then update ui, and fetch initial data
        getHeatMapDataTask.valueProperty().addListener(new ChangeListener<HeatMapQuantity[]>() {
            @Override public void changed(ObservableValue<? extends HeatMapQuantity[]> ov, HeatMapQuantity[] t, HeatMapQuantity[] results) {
                long max = Long.MIN_VALUE, min = Long.MAX_VALUE;
                for(HeatMapQuantity hmq: results) {
                    max = Math.max(max, hmq.getQuantity());
                    min = Math.min(min, hmq.getQuantity());
                }
                double maxScale = 1d/(double)max;
                double minScale = 1d/(double)min;

                for(HeatMapQuantity hmq: results) {
                    double v = Math.abs((double)hmq.getQuantity());
                    v = v / 10000;
                    v = 1 + (v*9);
                    v = Math.log10(v);

                    Color color;
                    if (hmq.getQuantity() == 0) {
                        color = Color.WHITE;
                    } else if (hmq.getQuantity() > 0) {
                        color = ladder(v, HOT_COLORS);
                    } else {
                        color = ladder(v, COLD_COLORS);
                    }
                    map.setStateColor(hmq.getStateProvCd(), color);
                    // update labels
                    Label stateLabel = stateLabelMap.get(hmq.getStateProvCd());
                    if (stateLabel!=null) stateLabel.setText(hmq.getQuantity()>0? "+"+hmq.getQuantity() : Long.toString(hmq.getQuantity()));
                }
            }
        });
        // start task
        new Thread(getHeatMapDataTask).start();
    }
    
    /**
     * Get the color at the give {@code position} in the ladder of color stops
     */
    private static Color ladder(final double position, final Stop[] stops) {
        Stop prevStop = null;
        for (int i=0; i<stops.length; i++) {
            Stop stop = stops[i];
            if(position <= stop.getOffset()){
                if (prevStop == null) {
                    return stop.getColor();
                } else {
                    return interpolateLinear((position-prevStop.getOffset())/(stop.getOffset()-prevStop.getOffset()), prevStop.getColor(), stop.getColor());
                }
            }
            prevStop = stop;
        }
        // position is greater than biggest stop, so will we biggest stop's color
        return prevStop.getColor();
    }
    
    /**
     * interpolate at a set {@code position} between two colors {@code color1} and {@code color2}.
     * The interpolation is done is linear RGB color space not the default sRGB color space.
     */
    private static Color interpolateLinear(double position, Color color1, Color color2) {
        Color c1Linear = convertSRGBtoLinearRGB(color1);
        Color c2Linear = convertSRGBtoLinearRGB(color2);
        return convertLinearRGBtoSRGB(Color.color(
            c1Linear.getRed()     + (c2Linear.getRed()     - c1Linear.getRed())     * position,
            c1Linear.getGreen()   + (c2Linear.getGreen()   - c1Linear.getGreen())   * position,
            c1Linear.getBlue()    + (c2Linear.getBlue()    - c1Linear.getBlue())    * position,
            c1Linear.getOpacity() + (c2Linear.getOpacity() - c1Linear.getOpacity()) * position
        ));
    }
    
    /**
     * Helper function to convert a color in sRGB space to linear RGB space.
     */
    public static Color convertSRGBtoLinearRGB(Color color) {
        double[] colors = new double[] { color.getRed(), color.getGreen(), color.getBlue() };
        for (int i=0; i<colors.length; i++) {
            if (colors[i] <= 0.04045) {
                colors[i] = colors[i] / 12.92;
            } else {
                colors[i] = Math.pow((colors[i] + 0.055) / 1.055, 2.4);
            }
        }
        return Color.color(colors[0], colors[1], colors[2], color.getOpacity());
    }
    
    /**
     * Helper function to convert a color in linear RGB space to SRGB space.
     */
    public static Color convertLinearRGBtoSRGB(Color color) {
        double[] colors = new double[] { color.getRed(), color.getGreen(), color.getBlue() };
        for (int i=0; i<colors.length; i++) {
            if (colors[i] <= 0.0031308) {
                colors[i] = colors[i] * 12.92;
            } else {
                colors[i] = (1.055 * Math.pow(colors[i], (1.0 / 2.4))) - 0.055;
            }
        }
        return Color.color(colors[0], colors[1], colors[2], color.getOpacity());
    }
    
    private static class Month {
        private static DateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy MMM");
        public final Date date;
        
        public Month(Date date) {
            this.date = date;
        }
        
        @Override public String toString() {
            return MONTH_FORMAT.format(date);
        }
    }
}
