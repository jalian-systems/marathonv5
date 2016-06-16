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

import com.javafx.experiments.dataapp.model.transit.ProductTypeTransitCumulativeSeriesSales;
import java.math.BigDecimal;
import java.util.List;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Special cell for displaying charts
 */
public class SeriesChartTableCellFactory implements Callback<TableColumn<ProductTypeTransitCumulativeSeriesSales, List<Double>>, TableCell<ProductTypeTransitCumulativeSeriesSales, List<Double>>> {

    @Override public TableCell<ProductTypeTransitCumulativeSeriesSales, List<Double>> call(TableColumn<ProductTypeTransitCumulativeSeriesSales, List<Double>> p) {
        return new SeriesChartTableCell();
    }
    
    private static class SeriesChartTableCell extends TableCell<ProductTypeTransitCumulativeSeriesSales, List<Double>> {

        NumberAxis xAxis;
        NumberAxis yAxis;
        AreaChart<Number, Number> chart;

        public SeriesChartTableCell() {
            super();
            getStyleClass().add("table-chart-cell");
            xAxis = new NumberAxis();
            yAxis= new NumberAxis();
            chart = new AreaChart<Number, Number>(xAxis, yAxis);
            chart.getStyleClass().add("table-chart");
            chart.setLegendVisible(false);
            chart.setTitle(null);
            chart.setAnimated(false);

            xAxis.setLabel(null);
            xAxis.setTickMarkVisible(false);
            xAxis.setMinorTickVisible(false);
            xAxis.setTickLabelFormatter(new StringConverter<Number>() { // TODO Remove after RT-16180 is fixed
                @Override public String toString(Number t) { return ""; }
                @Override public Number fromString(String string) { return 0; }
            });
    //        xAxis.setTickLabelsVisible(false); TODO Restore after RT-16180 is fixed

            yAxis.setLabel(null);
            yAxis.setTickMarkVisible(false);
            yAxis.setMinorTickVisible(false);
            yAxis.setTickLabelFormatter(new StringConverter<Number>() { // TODO Remove after RT-16180 is fixed
                @Override public String toString(Number t) { return ""; }
                @Override public Number fromString(String string) { return 0; }
            });
    //        yAxis.setTickLabelsVisible(false); TODO Restore after RT-16180 is fixed

            chart.setMinSize(20,35);
            chart.setPrefSize(150,35);
            chart.setMaxSize(Double.MAX_VALUE,35);

            setGraphic(chart);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

         }

        @Override public void updateItem(List<Double> item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                XYChart.Series<Number, Number> salesSeries = new XYChart.Series<Number, Number>();
                for (int i = 0; i < item.size(); i++) {
                    salesSeries.getData().add(new XYChart.Data<Number, Number>(i, item.get(i)));
                }
                chart.getData().setAll(salesSeries);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
        }

    }
}
