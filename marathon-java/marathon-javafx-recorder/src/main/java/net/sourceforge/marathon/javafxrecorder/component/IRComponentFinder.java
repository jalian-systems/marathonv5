package net.sourceforge.marathon.javafxrecorder.component;

import java.awt.Component;

public interface IRComponentFinder {

    Class<? extends RComponent> get(Component component);

}
