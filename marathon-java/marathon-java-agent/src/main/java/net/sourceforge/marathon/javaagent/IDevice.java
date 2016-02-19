package net.sourceforge.marathon.javaagent;

import java.awt.Component;

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

    void sendKeys(Component component, CharSequence... keysToSend);

    void pressKey(Component component, JavaAgentKeys keyToPress);

    void releaseKey(Component component, JavaAgentKeys keyToRelease);

    void buttonDown(Component component, Buttons button, int xoffset, int yoffset);

    void buttonUp(Component component, Buttons button, int xoffset, int yoffset);

    void moveto(Component component);

    void moveto(Component component, int xoffset, int yoffset);

    void click(Component component, Buttons button, int clickCount, int xoffset, int yoffset);

}