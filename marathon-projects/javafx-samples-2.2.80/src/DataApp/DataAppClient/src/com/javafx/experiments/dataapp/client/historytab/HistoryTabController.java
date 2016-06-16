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
package com.javafx.experiments.dataapp.client.historytab;

import com.javafx.experiments.dataapp.client.DataApplication;
import com.javafx.experiments.dataapp.client.rest.CumulativeLiveSalesClient;
import com.javafx.experiments.dataapp.model.ProductType;
import com.javafx.experiments.dataapp.model.Region;
import com.javafx.experiments.dataapp.model.transit.ProductTypeTransitCumulativeSeriesSales;
import com.javafx.experiments.dataapp.model.transit.RegionTransitCumulativeSales;
import com.javafx.experiments.dataapp.model.transit.StateTransitCumulativeSales;
import com.javafx.experiments.dataapp.model.transit.TransitCumulativeSales;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableView;
import javafx.util.Pair;
import javafx.util.StringConverter;

/**
 * Controller for the History Tab of the car sales application
 */
public class HistoryTabController implements Runnable, Initializable {
    private static final NumberFormat priceFormatter = new DecimalFormat("$#,###");
    private static final DateFormat monthFormat = new SimpleDateFormat("MMM");
    private static final DateFormat yearFormat = new SimpleDateFormat("yyyy");
    
    @FXML public TimeRangeSelector timeRangeSelector;
    @FXML public TableView<ProductTypeTransitCumulativeSeriesSales> dataTable;
    @FXML public PieChart pieChart1 = new PieChart();
    @FXML public PieChart pieChart2 = new PieChart();
    @FXML public ChoiceBox regionChoiceBox; 
    
    private final ObservableList<ProductTypeTransitCumulativeSeriesSales> dataTableData = FXCollections.observableArrayList();
    private final HashMap<ProductType, PieChart.Data> pieData1 = new HashMap<ProductType, PieChart.Data>();
    private final HashMap<Region, PieChart.Data> pieRegionData = new HashMap<Region, PieChart.Data>();
    private final HashMap<String, PieChart.Data> pieStateData = new HashMap<String, PieChart.Data>();
    private final CumulativeLiveSalesClient clsClient = new CumulativeLiveSalesClient();
    private final GetSaleService getSalesService = new GetSaleService(clsClient);
    private TransitCumulativeSales timelineCls[];
    private NumberAxis xAxis;
    private boolean pieTwoDisplayingRegions = true;
    
    @Override public void initialize(URL url, ResourceBundle rb) {
        // init table
        dataTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataTable.setItems(dataTableData);
        // init time range selector
        final XYChart.Series<Number, Number> salesSeries = new XYChart.Series<Number, Number>();
        final XYChart.Series<Number, Number> costsSeries = new XYChart.Series<Number, Number>();
        timeRangeSelector.setData(salesSeries, costsSeries);
        // configure xAxis
        xAxis = (NumberAxis)timeRangeSelector.getChart().getXAxis();
        xAxis.setAutoRanging(false);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override public String toString(Number t) {
                double decimalDays = t.doubleValue();
                Calendar date = convertFromDecimalMonth(decimalDays);
                if (date.get(Calendar.MONTH) == 0) {
                    return yearFormat.format(date.getTime());
                } else {
                    return monthFormat.format(date.getTime());
                }
            }

            @Override public Number fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        // set fixed range to start with
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2010, 0, 1, 0, 0, 0);
        xAxis.setLowerBound(convertToDecimalMonth(c));
        c.set(2012, 0, 1, 0, 0, 0);
        xAxis.setUpperBound(convertToDecimalMonth(c));
        // set major tick marks every month
        xAxis.setTickUnit(1);
        xAxis.setMinorTickCount(0);
        // listen to changes of time range and update charts
        timeRangeSelector.setDateChangeListener(this);
        //init regionbox
        regionChoiceBox.setItems(DataApplication.getAmericanRegions());
        regionChoiceBox.getSelectionModel().selectFirst();
        regionChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override public void changed(ObservableValue ov, Object oldValue, Object newValue) {
                getSalesService.setRegionSelection(newValue);
                pieChart2.getData().clear();
                pieRegionData.clear();
                Platform.runLater(HistoryTabController.this);
            }
        });
        // this needs to be run on FX thread 
        DataApplication.registerDataLoadingTask(new Runnable() {
            @Override public void run() {
                // fetch initial data in the background
                final Task<TransitCumulativeSales[]> getCumulativeSales = new Task<TransitCumulativeSales[]>() {
                    @Override protected TransitCumulativeSales[] call() throws Exception {
                        return clsClient.findAll_JSON(TransitCumulativeSales[].class);
                    }
                };
                // listen for when the top time range selector chart is populated with data
                getCumulativeSales.stateProperty().addListener(new ChangeListener<State>() {
                    @Override public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                        if (newState == State.SUCCEEDED) {
                            timelineCls = getCumulativeSales.getValue();
                            List<XYChart.Data<Number, Number>> costData = new ArrayList<XYChart.Data<Number, Number>>();
                            List<XYChart.Data<Number, Number>> salesData = new ArrayList<XYChart.Data<Number, Number>>();
                            for (int i = 0; i < timelineCls.length; i++) {
                                double day = convertToDecimalMonth(timelineCls[i].makeCalendar());
                                costData.add(new XYChart.Data<Number, Number>(day, timelineCls[i].getCost().doubleValue()));
                                salesData.add(new XYChart.Data<Number, Number>(day, timelineCls[i].getSales().doubleValue()));
                            }
                            xAxis.setLowerBound(convertToDecimalMonth(timelineCls[timelineCls.length-1].makeCalendar()));
                            xAxis.setUpperBound(convertToDecimalMonth(timelineCls[0].makeCalendar()));
                            timeRangeSelector.getChart().getYAxis().setAutoRanging(true);
                            costsSeries.getData().setAll(costData);
                            salesSeries.getData().setAll(salesData);
                            // move handles to sensible start positions, 80% and 85%
                            timeRangeSelector.setLeftValue(xAxis.getLowerBound()+(0.8*(xAxis.getUpperBound()-xAxis.getLowerBound())));
                            timeRangeSelector.setRightValue(xAxis.getLowerBound()+(0.85*(xAxis.getUpperBound()-xAxis.getLowerBound())));
                            // update everything
                            timeRangeSelector.getDateChangeListener().run();
                        } else if (newState == State.FAILED) {
                            System.err.println("Failed to get cumulative sales for History Tab.");
                            if (getCumulativeSales.getException() != null) 
                                    getCumulativeSales.getException().printStackTrace();
                        }
                    }
                });
                // listen 
                getSalesService.stateProperty().addListener(new ChangeListener<State>() {
                    @Override public void changed(ObservableValue<? extends State> ov, State t, State newState) {
                        if (newState == State.SUCCEEDED) {
                            final Pair<ProductTypeTransitCumulativeSeriesSales[], TransitCumulativeSales[]> results = getSalesService.getValue();
                            final ProductTypeTransitCumulativeSeriesSales[] ps = results.getKey();
                            // update pie 1
                            for (int i = 0; i < ps.length; i++) {
                                PieChart.Data data = pieData1.get(ps[i].getProductType());
                                if (data == null) {
                                    String classStr = ps[i].getProductType().getClass1();
                                    data = new PieChart.Data(classStr + ":" + ps[i].getProductType().getSubclass().charAt(0), ps[i].getSales().doubleValue());
                                    pieData1.put(ps[i].getProductType(), data);
                                    pieChart1.getData().add(data);

                                } else {
                                    data.setPieValue(ps[i].getSales().doubleValue());
                                }
                            }
                            // update pie 2
                            if (results.getValue() instanceof RegionTransitCumulativeSales[]){ // populate pie 2 with regions
                                if (!pieTwoDisplayingRegions) {
                                    pieTwoDisplayingRegions = true;
                                    pieChart2.getData().clear();
                                    pieRegionData.clear();
                                }
                                final RegionTransitCumulativeSales[] rs = (RegionTransitCumulativeSales[]) results.getValue();
                                for (int i = 0; i < rs.length; i++) {
                                    PieChart.Data data = pieRegionData.get(rs[i].getRegion());
                                    if (data == null) {
                                        data = new PieChart.Data(rs[i].getRegion().getName(), rs[i].getSales().doubleValue());
                                        pieRegionData.put(rs[i].getRegion(), data);
                                        pieChart2.getData().add(data);
                                    } else {
                                        data.setPieValue(rs[i].getSales().doubleValue());
                                    }
                                }
                            } else { // populate pie 2 with states
                                if (pieTwoDisplayingRegions) {
                                    pieTwoDisplayingRegions = false;
                                    pieChart2.getData().clear();
                                    pieStateData.clear();
                                }
                                final StateTransitCumulativeSales[] rs = (StateTransitCumulativeSales[]) results.getValue();
                                for (int i = 0; i < rs.length; i++) {    
                                    PieChart.Data data = pieStateData.get(rs[i].getState());
                                    if (data == null) {
                                        data = new PieChart.Data(rs[i].getState(), rs[i].getSales().doubleValue());
                                        pieStateData.put(rs[i].getState(), data);
                                        pieChart2.getData().add(data);
                                    } else {
                                        data.setPieValue(rs[i].getSales().doubleValue());
                                    }
                                }
                            }
                            // update table
                            dataTableData.setAll(ps);
                        } else if (newState == State.FAILED) {
                            System.err.println("Failed to get product and region sales for History Tab.");
                            if (getCumulativeSales.getException() != null) 
                                    getCumulativeSales.getException().printStackTrace();
                        }
                    }
                });
                // start fetching data for time range selector
                new Thread(getCumulativeSales).start();
            }
        });
    }
    
    /**
     * Convert a Java Date into a decimal number of days from beginning of year 0000
     * 
     * @param date The date to convert
     * @return date converted to decimal days
     */
    private static double convertToDecimalMonth(Calendar date) {
        int month = (date.get(Calendar.YEAR) * 12) + date.get(Calendar.MONTH);
        double day = ((double)date.get(Calendar.DAY_OF_MONTH)-1d) / ((double)date.getActualMaximum(Calendar.DAY_OF_MONTH)-1d);
        return month + day;
    }
    
    /**
     * Convert decimal days back to Java Date
     * 
     * @param date The decimal days to convert
     * @return The Java Date represented by the decimal days
     */
    private static Calendar convertFromDecimalMonth(double date) {
        Calendar c = Calendar.getInstance();
        c.clear();
        int yearMonth = (int)Math.floor(date);
        int year = (int)Math.floor(yearMonth/12d);
        c.set(Calendar.YEAR,year);
        int month = yearMonth - (year*12);
        c.set(Calendar.MONTH,month);
        int day = (int)(c.getActualMaximum(Calendar.DAY_OF_MONTH) * (date - yearMonth)) + 1;
        c.set(Calendar.DAY_OF_MONTH,day);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }
    
    /**
     * Start/Restart background service to update the table and pie charts when time range changes
     */
    @Override public void run() {
        // clear existing data so progress indicator is shown
        dataTableData.clear();
        // calculate the data base ids for the selected range
        final double start = timeRangeSelector.getLeftValue().get();
        final double end = timeRangeSelector.getRightValue().get();
        double totalSize = xAxis.getUpperBound() - xAxis.getLowerBound();
        double startIdx = ((start-xAxis.getLowerBound())/totalSize) * timelineCls.length;
        double endIdx = ((end-xAxis.getLowerBound())/totalSize) * timelineCls.length;
        int startDayId = timelineCls[(int)endIdx].getStartDailySalesId();
        int endDayId = timelineCls[(int)startIdx].getEndDailySalesId();
        // setup data service and start it
        getSalesService.setRange(startDayId, endDayId);
        getSalesService.setRegionSelection(regionChoiceBox.getSelectionModel().getSelectedItem());
        getSalesService.restart();
    }
}
