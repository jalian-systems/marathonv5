package net.sourceforge.marathon.javafxagent;

import javafx.event.EventTarget;
import javafx.scene.input.MouseButton;

public interface IDevice {

    enum Buttons {
        LEFT(0), MIDDLE(1), RIGHT(2);

        private int button;

        Buttons(int button) {
            this.button = button;
        }

        public int getButton() {
            return button;
        }

        public static Buttons getButtonFor(int button) {
            if (button == 0)
                return LEFT;
            else if (button == 1)
                return MIDDLE;
            else if (button == 2)
                return RIGHT;
            throw new JavaAgentException("JavaAgent currently does not support more than 3 mouse buttons", null);
        }

        public MouseButton getMouseButton() {
            if (button == 0)
                return MouseButton.PRIMARY;
            else if (button == 1)
                return MouseButton.MIDDLE;
            else
                return MouseButton.SECONDARY;
        }
    }

    void sendKeys(EventTarget component, CharSequence... keysToSend);

    void pressKey(EventTarget component, JavaAgentKeys keyToPress);

    void releaseKey(EventTarget component, JavaAgentKeys keyToRelease);

    void buttonDown(EventTarget component, Buttons button, double xoffset, double yoffset);

    void buttonUp(EventTarget component, Buttons button, double xoffset, double yoffset);

    void moveto(EventTarget component);

    void moveto(EventTarget component, double xoffset, double yoffset);

    void click(EventTarget component, Buttons button, int clickCount, double xoffset, double yoffset);

}