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
package net.sourceforge.marathon.contextmenu;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import net.sourceforge.marathon.component.RComponent;

public class TransparentFrame implements AWTEventListener {

    private static final Logger logger = Logger.getLogger(TransparentFrame.class.getName());

    private RComponent component;
    private boolean disposed = false;

    private Graphics graphics;
    private static final Color BG = new Color(1.0f, 0.0f, 0.0f, 0.6f);

    public TransparentFrame(RComponent RComponent) {
        this.component = RComponent;
        graphics = component.getComponent().getGraphics().create();
    }

    public void setVisible(boolean b) {
        if (b) {
            Toolkit.getDefaultToolkit().addAWTEventListener(this, AWTEvent.PAINT_EVENT_MASK);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    paintTransparentFrame();
                }
            });
        } else {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        }
    }

    public void dispose() {
        disposed = true;
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                paintTransparentFrame();
            }
        });
    }

    public void eventDispatched(AWTEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                paintTransparentFrame();
            }
        });
    }

    protected void paintTransparentFrame() {
        Dimension size = component.getComponent().getSize();
        if (component.getComponent() instanceof JComponent) {
            JComponent jc = (JComponent) component.getComponent();
            jc.paintImmediately(0, 0, size.width, size.height);
        }
        if (disposed)
            return;
        size = component.getSize();
        Point location = component.getLocation();
        graphics.setColor(BG);
        graphics.fillRect(0 + location.x, 0 + location.y, size.width, size.height);
        String name = component.getRComponentName();
        graphics.setColor(Color.WHITE);
        Font font = new Font(graphics.getFont().getName(), Font.ITALIC | Font.BOLD, 12);
        FontMetrics metrics = graphics.getFontMetrics(font);
        int stringWidth = metrics.stringWidth(name);
        int stringHeight = metrics.getHeight() / 2;
        if (stringWidth < size.width && stringHeight < size.height) {
            graphics.setFont(font);
            graphics.drawString(name, (size.width - stringWidth) / 2 + location.x, (size.height + stringHeight) / 2 + location.y);
        } else if (stringWidth >= size.width || stringHeight >= size.height) {
            graphics.setFont(new Font(graphics.getFont().getName(), Font.ITALIC, 9));
            graphics.drawString(name, 0 + location.x, (size.height + stringHeight) / 2 + location.y);
        } else
            logger.warning("Not drawing");
    }
}
