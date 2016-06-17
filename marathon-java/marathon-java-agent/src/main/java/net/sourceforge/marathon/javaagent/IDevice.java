/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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