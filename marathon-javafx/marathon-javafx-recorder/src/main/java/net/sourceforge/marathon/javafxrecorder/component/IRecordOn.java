package net.sourceforge.marathon.javafxrecorder.component;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public interface IRecordOn {
    Node getRecordOn(Node component, Point2D point);
}