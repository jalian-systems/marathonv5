/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.javaagent.components;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.InvalidElementStateException;

public class JEditorPanePosJavaElement extends AbstractJavaElement {

    private int pos;
    private AbstractJavaElement parent;

    public JEditorPanePosJavaElement(net.sourceforge.marathon.javaagent.components.JEditorPaneJavaElement parent, int pos) {
        super(parent);
        this.parent = parent;
        this.pos = pos;
    }

    @Override public Object _makeVisible() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        try {
            Rectangle bounds = editor.modelToView(pos);
            if (bounds != null) {
                bounds.height = editor.getVisibleRect().height;
                editor.scrollRectToVisible(bounds);
            }
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Invalid position " + pos + "(" + e.getMessage() + ")", e);
        }
        return null;
    }

    @Override public Point _getMidpoint() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        try {
            Rectangle bounds = editor.modelToView(pos);
            return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Invalid position " + pos + "(" + e.getMessage() + ")", e);
        }
    }

    @Override public void _moveto() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        try {
            Rectangle bounds = editor.modelToView(pos);
            getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Invalid position " + pos + "(" + e.getMessage() + ")", e);
        }
    }

}
