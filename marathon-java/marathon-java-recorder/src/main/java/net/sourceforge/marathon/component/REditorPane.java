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
package net.sourceforge.marathon.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;

import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public class REditorPane extends RComponent {

    public static final Logger LOGGER = Logger.getLogger(REditorPane.class.getName());

    private int linkPosition;
    private String hRef;
    private String text;
    private char SEPARATER = ',';
    private int hRefIndex;
    private int textIndex;

    public REditorPane(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source, omapConfig, point, recorder);
        if (!isHtmlDocument()) {
            return;
        }
        if (point != null) {
            int location = ((JEditorPane) component).viewToModel(point);
            Document document = ((JEditorPane) component).getDocument();
            setHRef(location, document);
            linkPosition = location;
        }
    }

    @Override
    protected void mouseButton1Pressed(MouseEvent me) {
        int location = ((JEditorPane) component).viewToModel(me.getPoint());
        Document document = ((JEditorPane) component).getDocument();
        setHRef(location, document);
        linkPosition = location;
        recorder.recordClick2(this, me, true);
    }

    @Override
    public String getCellInfo() {
        if (text != null && !"".equals(text)) {
            return "text=" + text + (textIndex > 0 ? "(" + textIndex + ")" : "");
        }
        if (hRef != null && !"".equals(hRef)) {
            return "link=" + hRef + (hRefIndex > 0 ? "(" + hRefIndex + ")" : "");
        }
        String result = linkPosition > 0 ? "" + linkPosition : null;
        if (hRef != null && !hRef.equals("")) {
            result = hRef + SEPARATER + result;
        }
        return result;

    }

    public boolean isHtmlDocument() {
        return "text/html".equalsIgnoreCase(((JEditorPane) component).getContentType());
    }

    public void setHRef(int pos, Document doc) {
        hRef = null;
        text = null;
        if (!(doc instanceof HTMLDocument)) {
            return;
        }
        HTMLDocument hdoc = (HTMLDocument) doc;
        Iterator iterator = hdoc.getIterator(HTML.Tag.A);
        while (iterator.isValid()) {
            if (pos >= iterator.getStartOffset() && pos < iterator.getEndOffset()) {
                AttributeSet attributes = iterator.getAttributes();
                if (attributes != null && attributes.getAttribute(HTML.Attribute.HREF) != null) {
                    try {
                        text = hdoc.getText(iterator.getStartOffset(), iterator.getEndOffset() - iterator.getStartOffset()).trim();
                        hRef = attributes.getAttribute(HTML.Attribute.HREF).toString();
                        setIndexOfHrefAndText(hdoc, pos, text, hRef);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            }
            iterator.next();
        }
    }

    private void setIndexOfHrefAndText(HTMLDocument hdoc, int pos, String text, String hRef) {
        this.hRefIndex = 0;
        this.textIndex = 0;
        Iterator iterator = hdoc.getIterator(HTML.Tag.A);
        while (iterator.isValid()) {
            if (pos >= iterator.getStartOffset() && pos < iterator.getEndOffset()) {
                return;
            } else {
                AttributeSet attributes = iterator.getAttributes();
                if (attributes != null && attributes.getAttribute(HTML.Attribute.HREF) != null) {
                    try {
                        String t = hdoc.getText(iterator.getStartOffset(), iterator.getEndOffset() - iterator.getStartOffset())
                                .trim();
                        String h = attributes.getAttribute(HTML.Attribute.HREF).toString();
                        if (t.equals(text)) {
                            this.textIndex++;
                        }
                        if (h.equals(hRef)) {
                            this.hRefIndex++;
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
            iterator.next();
        }
    }

    @Override
    public String toString() {
        return "REditorPane [linkPosition=" + linkPosition + ", hRef=" + hRef + ", text=" + text + ", SEPARATER=" + SEPARATER
                + ", hRefIndex=" + hRefIndex + ", textIndex=" + textIndex + "]";
    }

    @Override
    public String getCText() {
        return null;
    }
}
