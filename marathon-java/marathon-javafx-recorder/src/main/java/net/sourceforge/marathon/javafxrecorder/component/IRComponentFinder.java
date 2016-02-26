package net.sourceforge.marathon.javafxrecorder.component;

import javafx.scene.Node;

public interface IRComponentFinder {

    Class<? extends RComponent> get(Node component);

}
