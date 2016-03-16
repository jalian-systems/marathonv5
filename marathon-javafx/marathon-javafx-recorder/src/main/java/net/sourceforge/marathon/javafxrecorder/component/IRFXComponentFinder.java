package net.sourceforge.marathon.javafxrecorder.component;

import javafx.scene.Node;

public interface IRFXComponentFinder extends IRecordOn {

    Class<? extends RFXComponent> get(Node component);
}
