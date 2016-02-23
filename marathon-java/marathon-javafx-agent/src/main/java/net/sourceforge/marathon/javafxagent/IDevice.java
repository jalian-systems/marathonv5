package net.sourceforge.marathon.javafxagent;

import javafx.scene.Node;

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
    }

    void sendKeys(Node component, CharSequence... keysToSend);

    void pressKey(Node component, JavaAgentKeys keyToPress);

    void releaseKey(Node component, JavaAgentKeys keyToRelease);

    void buttonDown(Node component, Buttons button, double xoffset, double yoffset);

    void buttonUp(Node component, Buttons button, double xoffset, double yoffset);

    void moveto(Node component);

    void moveto(Node component, int xoffset, int yoffset);

    void click(Node component, Buttons button, int clickCount, double xoffset, double yoffset);

}