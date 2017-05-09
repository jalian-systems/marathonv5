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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.IPseudoElement;
import net.sourceforge.marathon.javaagent.InvalidElementStateException;
import net.sourceforge.marathon.javaagent.NoSuchElementException;

public class JEditorPaneTagJavaElement extends AbstractJavaElement implements IPseudoElement {

    public static final Logger LOGGER = Logger.getLogger(JEditorPaneTagJavaElement.class.getName());

    private int index;
    private Tag tag;
    private JEditorPaneJavaElement parent;

    public JEditorPaneTagJavaElement(JEditorPaneJavaElement parent, Tag tag, int index) {
        super(parent);
        this.parent = parent;
        this.tag = tag;
        this.index = index;
    }

    @Override public String _getText() {
        Iterator iterator = findTag((HTMLDocument) ((JEditorPane) parent.getComponent()).getDocument());
        int startOffset = iterator.getStartOffset();
        int endOffset = iterator.getEndOffset();
        try {
            return ((HTMLDocument) ((JEditorPane) parent.getComponent()).getDocument()).getText(startOffset,
                    endOffset - startOffset);
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Unable to get text for tag " + tag + " in document with index " + index, e);
        }
    }

    private Iterator findTag(HTMLDocument doc) {
        Iterator iterator = doc.getIterator(tag);
        int current = 0;
        while (iterator.isValid()) {
            if (current++ == index) {
                break;
            }
            iterator.next();
        }
        if (!iterator.isValid()) {
            throw new NoSuchElementException("Unable to find tag " + tag + " in document with index " + index, null);
        }
        return iterator;
    }

    @Override public IJavaElement getParent() {
        return parent;
    }

    @Override public String createHandle() {
        JSONObject o = new JSONObject().put("selector", "tag").put("parameters",
                new JSONArray().put(tag.toString()).put(index + 1));
        return parent.getHandle() + "#" + o.toString();
    }

    @Override public String getAttribute(final String name) {
        if ("text".equals(name)) {
            return getText();
        }
        if ("hRefIndex".equals(name)) {
            return getHRefIndex() + "";
        }
        if ("textIndex".equals(name)) {
            return getTextIndex() + "";
        }
        return EventQueueWait.exec(new Callable<String>() {
            @Override public String call() throws Exception {
                Iterator iterator = findTag((HTMLDocument) ((JEditorPane) parent.getComponent()).getDocument());
                AttributeSet attributes = iterator.getAttributes();
                Attribute attr = findAttribute(name);
                if (attr != null && attributes.isDefined(attr)) {
                    return attributes.getAttribute(attr).toString();
                }
                return null;
            }
        });
    }

    @Override public Object _makeVisible() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        Iterator iterator = findTag((HTMLDocument) editor.getDocument());
        int startOffset = iterator.getStartOffset();
        int endOffset = iterator.getEndOffset();
        try {
            Rectangle bounds = editor.modelToView(startOffset + (endOffset - startOffset) / 2);
            if (bounds != null) {
                bounds.height = editor.getVisibleRect().height;
                editor.scrollRectToVisible(bounds);
            }
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Unable to get text for tag " + tag + " in document with index " + index, e);
        }
        return null;
    }

    @Override public void _moveto() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        Iterator iterator = findTag((HTMLDocument) editor.getDocument());
        int startOffset = iterator.getStartOffset();
        int endOffset = iterator.getEndOffset();
        try {
            Rectangle bounds = editor.modelToView(startOffset + (endOffset - startOffset) / 2);
            getDriver().getDevices().moveto(parent.getComponent(), bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Unable to get text for tag " + tag + " in document with index " + index, e);
        }
    }

    @Override public boolean _isEnabled() {
        return true;
    }

    @Override public Component getPseudoComponent() {
        throw new RuntimeException("No physical pseudo component available for JEditorPane tag items");
    }

    static final Attribute allAttributes[] = { HTML.Attribute.FACE, HTML.Attribute.COMMENT, HTML.Attribute.SIZE,
            HTML.Attribute.COLOR, HTML.Attribute.CLEAR, HTML.Attribute.BACKGROUND, HTML.Attribute.BGCOLOR, HTML.Attribute.TEXT,
            HTML.Attribute.LINK, HTML.Attribute.VLINK, HTML.Attribute.ALINK, HTML.Attribute.WIDTH, HTML.Attribute.HEIGHT,
            HTML.Attribute.ALIGN, HTML.Attribute.NAME, HTML.Attribute.HREF, HTML.Attribute.REL, HTML.Attribute.REV,
            HTML.Attribute.TITLE, HTML.Attribute.TARGET, HTML.Attribute.SHAPE, HTML.Attribute.COORDS, HTML.Attribute.ISMAP,
            HTML.Attribute.NOHREF, HTML.Attribute.ALT, HTML.Attribute.ID, HTML.Attribute.SRC, HTML.Attribute.HSPACE,
            HTML.Attribute.VSPACE, HTML.Attribute.USEMAP, HTML.Attribute.LOWSRC, HTML.Attribute.CODEBASE, HTML.Attribute.CODE,
            HTML.Attribute.ARCHIVE, HTML.Attribute.VALUE, HTML.Attribute.VALUETYPE, HTML.Attribute.TYPE, HTML.Attribute.CLASS,
            HTML.Attribute.STYLE, HTML.Attribute.LANG, HTML.Attribute.DIR, HTML.Attribute.DECLARE, HTML.Attribute.CLASSID,
            HTML.Attribute.DATA, HTML.Attribute.CODETYPE, HTML.Attribute.STANDBY, HTML.Attribute.BORDER, HTML.Attribute.SHAPES,
            HTML.Attribute.NOSHADE, HTML.Attribute.COMPACT, HTML.Attribute.START, HTML.Attribute.ACTION, HTML.Attribute.METHOD,
            HTML.Attribute.ENCTYPE, HTML.Attribute.CHECKED, HTML.Attribute.MAXLENGTH, HTML.Attribute.MULTIPLE,
            HTML.Attribute.SELECTED, HTML.Attribute.ROWS, HTML.Attribute.COLS, HTML.Attribute.DUMMY, HTML.Attribute.CELLSPACING,
            HTML.Attribute.CELLPADDING, HTML.Attribute.VALIGN, HTML.Attribute.HALIGN, HTML.Attribute.NOWRAP, HTML.Attribute.ROWSPAN,
            HTML.Attribute.COLSPAN, HTML.Attribute.PROMPT, HTML.Attribute.HTTPEQUIV, HTML.Attribute.CONTENT,
            HTML.Attribute.LANGUAGE, HTML.Attribute.VERSION, HTML.Attribute.N, HTML.Attribute.FRAMEBORDER,
            HTML.Attribute.MARGINWIDTH, HTML.Attribute.MARGINHEIGHT, HTML.Attribute.SCROLLING, HTML.Attribute.NORESIZE,
            HTML.Attribute.ENDTAG };

    private Attribute findAttribute(String attrName) {
        for (Attribute attr : allAttributes) {
            if (attrName.toUpperCase().equals(attr.toString().toUpperCase())) {
                return attr;
            }
        }
        return null;
    }

    @Override public Point _getMidpoint() {
        JEditorPane editor = (JEditorPane) parent.getComponent();
        Iterator iterator = findTag((HTMLDocument) editor.getDocument());
        int startOffset = iterator.getStartOffset();
        int endOffset = iterator.getEndOffset();
        try {
            Rectangle bounds = editor.modelToView(startOffset + (endOffset - startOffset) / 2);
            return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        } catch (BadLocationException e) {
            throw new InvalidElementStateException("Unable to get text for tag " + tag + " in document with index " + index + "("
                    + "StartOffset: " + startOffset + " EndOffset: " + endOffset + ")", e);
        }
    }

    public int getHRefIndex() {
        return EventQueueWait.exec(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                String href = getAttribute("href");
                int hRefIndex = 0;
                int current = 0;
                JEditorPane editor = (JEditorPane) parent.getComponent();
                HTMLDocument document = (HTMLDocument) editor.getDocument();
                Iterator iterator = document.getIterator(Tag.A);
                while (iterator.isValid()) {
                    if (current++ >= index) {
                        return hRefIndex;
                    }
                    AttributeSet attributes = iterator.getAttributes();
                    if (attributes != null) {
                        Object attributeObject = attributes.getAttribute(HTML.Attribute.HREF);
                        if (attributeObject != null) {
                            String attribute = attributeObject.toString();
                            if (attribute.equals(href)) {
                                hRefIndex++;
                            }
                        }
                    }
                    iterator.next();
                }
                return -1;
            }
        });
    }

    public int getTextIndex() {
        return EventQueueWait.exec(new Callable<Integer>() {
            @Override public Integer call() throws Exception {
                String href = getText();
                int hRefIndex = 0;
                int current = 0;
                JEditorPane editor = (JEditorPane) parent.getComponent();
                HTMLDocument document = (HTMLDocument) editor.getDocument();
                Iterator iterator = document.getIterator(Tag.A);
                while (iterator.isValid()) {
                    if (current++ >= index) {
                        return hRefIndex;
                    }
                    String attribute = ((HTMLDocument) ((JEditorPane) parent.getComponent()).getDocument())
                            .getText(iterator.getStartOffset(), iterator.getEndOffset() - iterator.getStartOffset());
                    if (attribute != null && attribute.equals(href)) {
                        hRefIndex++;
                    }
                    iterator.next();
                }
                return -1;
            }
        });
    }
}
