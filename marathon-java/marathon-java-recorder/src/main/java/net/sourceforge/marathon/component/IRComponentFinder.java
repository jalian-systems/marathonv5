package net.sourceforge.marathon.component;

import java.awt.Component;

public interface IRComponentFinder {

    Class<? extends RComponent> get(Component component);

}
