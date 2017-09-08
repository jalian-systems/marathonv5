package net.sourceforge.marathon.fx.api;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.Region;

public class ButtonBarX extends ButtonBar {

    @Override protected double computePrefWidth(double height) {
        resizeButtons();
        return super.computePrefWidth(height);
    }

    private void resizeButtons() {
        double buttonMinWidth = -1;
        final List<? extends Node> buttons = getButtons();

        // determine the widest button
        double widest = buttonMinWidth;
        for (Node button : buttons) {
            if (ButtonBar.isButtonUniformSize(button)) {
                widest = Math.max(button.prefWidth(-1), widest);
            }
        }

        // set the width of all buttons
        for (Node button : buttons) {
            if (ButtonBar.isButtonUniformSize(button)) {
                sizeButton(button, widest);
            }
        }
    }

    private void sizeButton(Node btn, double pref) {
        if (btn instanceof Region) {
            Region regionBtn = (Region) btn;
            regionBtn.setPrefWidth(pref);
        }
    }

}
