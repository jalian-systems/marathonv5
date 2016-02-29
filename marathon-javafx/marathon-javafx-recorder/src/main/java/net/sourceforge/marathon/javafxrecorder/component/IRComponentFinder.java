package net.sourceforge.marathon.javafxrecorder.component;

import javafx.scene.Node;

public interface IRComponentFinder {

    Class<? extends RFXComponent> get(Node component);

}
