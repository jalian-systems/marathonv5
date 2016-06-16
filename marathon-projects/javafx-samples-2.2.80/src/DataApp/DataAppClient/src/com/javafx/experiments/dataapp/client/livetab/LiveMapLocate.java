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

import java.math.BigDecimal;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

/**
 * Helper class to draw sales on the map
 */
public class LiveMapLocate {
    
    //westernmost point in US: Cape Alava, Washington 48.164167,124.733056
    private static final double usLongitudeOrigin = 124.733056;
    //northernmost point in US: Lake of the Woods, Minnesota: 49.384358, 95.153314 
    private static final double usLatitudeOrigin = 49.384358;
    //easternmost point in US: Lubec, Maine: 44.817419, 66.949895 	
    private static final double usLongitudeMaximum = 66.949895;
    //southernmost point in US: Key West, Florida: 24.544701, 81.810333
    private static final double usLatitudeMaximum = 24.544701;
    
    //range of longitude
    private static final double rangeLong = usLongitudeMaximum - usLongitudeOrigin;
    //range of latitude
    private static final double rangeLat = usLatitudeMaximum - usLatitudeOrigin ; 
    
    //instance variables
    final private double rangeX;
    final private double rangeY;
    final private double scaleLongToX;
    final private double scaleLatToY;
    
    private Bounds bounds;
    private Group group;
    
    
            //Sanity Checks --------------------------------------------------------
//        //Boston
//        //42.357778, -71.061667        
//        BigDecimal BostonLat = new BigDecimal(-71.061667);
//        BigDecimal BostonLong = new BigDecimal(42.357778);
//        liveMapLocate.drawComplex(BostonLat, BostonLong);
//        
//        //Seattle
//        //47.609722,  -122.333056
//        BigDecimal SeattleLat = new BigDecimal(-122.333056);
//        BigDecimal SeattleLong = new BigDecimal(47.609722);
//        liveMapLocate.drawComplex(SeattleLat, SeattleLong);
//        
//        //San Fran
//        //37.7793, -122.4192
//        BigDecimal SFLat = new BigDecimal(-122.4192);
//        BigDecimal SFLong = new BigDecimal(37.7793);
//        liveMapLocate.drawComplex(localBounds, SFLat, SFLong);
//        
//        //Miami
//        BigDecimal MiamiLat = new BigDecimal(-80.224167);
//        BigDecimal MiamiLong = new BigDecimal(25.787778);
//        liveMapLocate.drawComplex(MiamiLat, MiamiLong);
//        
//        //Lubec Maine
//        BigDecimal LubecLat = new BigDecimal(-67.015556);
//        BigDecimal LubecLong = new BigDecimal(44.840833);
//        liveMapLocate.drawComplex(LubecLat, LubecLong);
//        
//        //Lake of hte Woods MN
//        BigDecimal LWLat = new BigDecimal(-95.153314);
//        BigDecimal LWLong = new BigDecimal(49.384358);
//        liveMapLocate.drawComplex(LWLat, LWLong);
    
    
    public LiveMapLocate(Group group, Bounds bounds){
        
        rangeX = bounds.getMinX() - bounds.getMaxX();
        rangeY = bounds.getMinY() - bounds.getMaxY();
        
        scaleLongToX = rangeX/rangeLong;
        scaleLatToY = rangeY/rangeLat;
        
        this.bounds = bounds;
        this.group = group;
              
    }
        
    public void drawComplex(Double longitude, Double latitude, boolean isNew, Color circleColor){
        if (longitude == null || latitude == null) return;
        //Longitude comes in negative
        double locationLong = longitude.doubleValue() * -1;
        //Latitude comes in positive
        double locationLat = latitude.doubleValue() ;
        
        //if the location is outside the continental us, ignore
        if ((locationLong > usLongitudeOrigin || locationLong < usLongitudeMaximum) || (locationLat < usLatitudeMaximum || locationLat > usLatitudeOrigin)) {
            return;
        }
        
        final double translatedLongitude = ((usLongitudeOrigin - locationLong)  * scaleLongToX) + bounds.getMinX();
        final double translatedLatitude = ((usLatitudeOrigin -locationLat ) *scaleLatToY) + bounds.getMinY();
        
        double startRadius = 30;
        double endShrinkRadius = 6;
        double endGrowthRadius = 600;
        double endOpacity = .3;
        
        if (circleColor == null) circleColor = Color.WHITE;
        
        //circles
        final Circle shrinkCircle = new Circle();
        shrinkCircle.setTranslateX(translatedLongitude);
        shrinkCircle.setTranslateY(translatedLatitude);
        shrinkCircle.setRadius(startRadius);
        shrinkCircle.setFill(
                new RadialGradient(
                    0, 0, 0.3, 0.3, 0.8, true, 
                    CycleMethod.NO_CYCLE, 
                    new Stop(0,Color.WHITE),
                    new Stop(0.1,circleColor.deriveColor(0, 0.9, 1.8, 1)),
                    new Stop(0.8,circleColor),
                    new Stop(1,circleColor.deriveColor(0, 1, 0.9, 1))
                ));

        group.getChildren().add(shrinkCircle);
        
        if (isNew) {
            final Circle growthCircle1 = new Circle();
            growthCircle1.setTranslateX(translatedLongitude);
            growthCircle1.setTranslateY(translatedLatitude);
            growthCircle1.setFill(null);
            growthCircle1.setStroke(circleColor);
            growthCircle1.setStrokeWidth(3);
            growthCircle1.setRadius(1);
            growthCircle1.setVisible(true);

            final Circle growthCircle2 = new Circle();
            growthCircle2.setTranslateX(translatedLongitude);
            growthCircle2.setTranslateY(translatedLatitude);
            growthCircle2.setFill(null);
            growthCircle2.setStroke(circleColor);
            growthCircle2.setStrokeWidth(3);
            growthCircle2.setRadius(1);
            growthCircle2.setVisible(false);

            final Circle growthCircle3 = new Circle();
            growthCircle3.setTranslateX(translatedLongitude);
            growthCircle3.setTranslateY(translatedLatitude);
            growthCircle3.setFill(null);
            growthCircle3.setStroke(circleColor);
            growthCircle3.setStrokeWidth(3);
            growthCircle3.setRadius(1);
            growthCircle3.setVisible(false);
            
            final Group circleGroup = new Group(growthCircle1, growthCircle2, growthCircle3);
            Color lighter = circleColor.deriveColor(0, 1, 1.5, 1);
            
            circleGroup.setEffect(
                    DropShadowBuilder.create().blurType(BlurType.TWO_PASS_BOX).radius(30).spread(0.6).color(lighter).build());
            group.getChildren().add(circleGroup);
            
            shrinkCircle.setEffect(DropShadowBuilder.create().blurType(BlurType.ONE_PASS_BOX).color(circleColor).radius(5).build());
        
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
               //shrinking
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(shrinkCircle.radiusProperty(), 0, Interpolator.EASE_OUT)),    
                new KeyFrame(Duration.millis(100), 
                    new KeyValue(shrinkCircle.radiusProperty(), startRadius*1.2, Interpolator.EASE_OUT)),    
                new KeyFrame(Duration.millis(150), 
                    new KeyValue(shrinkCircle.radiusProperty(), startRadius, Interpolator.EASE_OUT)),    
                new KeyFrame(Duration.millis(3000), 
                    new KeyValue(shrinkCircle.radiusProperty(), startRadius*.8, Interpolator.EASE_IN)),
                new KeyFrame(Duration.millis(6000), 
                    new KeyValue(shrinkCircle.radiusProperty(), endShrinkRadius, Interpolator.EASE_IN)),
                //growing
                new KeyFrame(Duration.millis(100),
                    new KeyValue(growthCircle1.radiusProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle1.visibleProperty(), true),
                    new KeyValue(growthCircle1.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(4000),
                    new KeyValue(growthCircle1.radiusProperty(), endGrowthRadius, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle1.opacityProperty(), 0, Interpolator.EASE_OUT)),

                new KeyFrame(Duration.millis(2000),
                    new KeyValue(growthCircle2.radiusProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle2.visibleProperty(), true),
                    new KeyValue(growthCircle2.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(5000),
                    new KeyValue(growthCircle2.radiusProperty(), endGrowthRadius, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle2.opacityProperty(), 0, Interpolator.EASE_OUT)),

                new KeyFrame(Duration.millis(3000),
                    new KeyValue(growthCircle3.radiusProperty(), 1, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle3.visibleProperty(), true),
                    new KeyValue(growthCircle3.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(6000),new EventHandler<ActionEvent>() {
                        @Override public void handle(ActionEvent t) {
                            group.getChildren().remove(circleGroup);
                            shrinkCircle.setEffect(null);
                        }
                    },
                    new KeyValue(growthCircle3.radiusProperty(), endGrowthRadius, Interpolator.EASE_OUT),
                    new KeyValue(growthCircle3.opacityProperty(), 0, Interpolator.EASE_OUT))
            );
       
            timeline.setCycleCount(1);
            timeline.play();  
        } else {
            shrinkCircle.setRadius(endShrinkRadius);
        }
    }
}
