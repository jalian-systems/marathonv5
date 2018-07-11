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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.AbstractJavaElement;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaTargetLocator.JWindow;
import net.sourceforge.marathon.javaagent.UnsupportedCommandException;

public class JEditorPaneJavaElement extends AbstractJavaElement {

    public static final Logger LOGGER = Logger.getLogger(JEditorPaneJavaElement.class.getName());

    private static final class PropertyPredicate implements Predicate {
        private final Properties p;

        private PropertyPredicate(Properties p) {
            this.p = p;
        }

        @Override
        public boolean isValid(JEditorPaneTagJavaElement e) {
            Enumeration<Object> keys = p.keys();
            while (keys.hasMoreElements()) {
                String object = (String) keys.nextElement();
                if (!p.getProperty(object).equals(e.getAttribute(object))) {
                    return false;
                }
            }
            return true;
        }
    }

    private static interface Predicate {
        public boolean isValid(JEditorPaneTagJavaElement e);
    }

    public JEditorPaneJavaElement(Component component, IJavaAgent driver, JWindow window) {
        super(component, driver, window);
    }

    @Override
    public List<IJavaElement> getByPseudoElement(String selector, Object[] params) {
        if (selector.equals("tag")) {
            final ArrayList<IJavaElement> r = new ArrayList<IJavaElement>();
            if (!(((JEditorPane) getComponent()).getDocument() instanceof HTMLDocument)) {
                return r;
            }
            final Tag tag = findTag((String) params[0]);
            if (tag == null) {
                return r;
            }
            if (params.length == 1) {
                EventQueueWait.exec(new Runnable() {
                    @Override
                    public void run() {
                        fillElements(tag, r, new Predicate() {
                            @Override
                            public boolean isValid(JEditorPaneTagJavaElement e) {
                                return true;
                            }
                        });
                    }
                });
            } else {
                r.add(new JEditorPaneTagJavaElement(this, tag, ((Integer) params[1]).intValue() - 1));
            }
            return r;
        } else if (selector.equals("select-by-properties")) {
            final ArrayList<IJavaElement> r = new ArrayList<IJavaElement>();
            if (!(((JEditorPane) getComponent()).getDocument() instanceof HTMLDocument)) {
                return r;
            }
            JSONObject o = new JSONObject((String) params[0]);
            return selectByProperties(new ArrayList<IJavaElement>(), o);
        }
        throw new UnsupportedCommandException("JEditorPane does not support pseudoelement " + selector, null);
    }

    private List<IJavaElement> selectByProperties(final ArrayList<IJavaElement> r, JSONObject o) {
        final Properties p;
        if (o.has("select")) {
            String spec = o.getString("select");
            if (!spec.startsWith("text=") && !spec.startsWith("link=")) {
                int pos = Integer.parseInt(spec);
                return Arrays.asList((IJavaElement) new JEditorPanePosJavaElement(this, pos));
            }
            p = parseSelectProperties(spec);
        } else {
            p = PropertyHelper.asProperties(o);
        }
        EventQueueWait.exec(new Runnable() {
            @Override
            public void run() {
                fillElements(Tag.A, r, new PropertyPredicate(p));
            }
        });
        return r;
    }

    private Properties parseSelectProperties(String spec) {
        Properties p = new Properties();
        String hRef = null;
        String text = null;
        int hRefIndex = 0;
        int textIndex = 0;
        boolean isText = false;
        if (spec.startsWith("text=")) {
            isText = true;
            spec = spec.substring(5);
            text = spec;
        } else if (spec.startsWith("link=")) {
            isText = false;
            spec = spec.substring(5);
            hRef = spec;
        } else {
            return p;
        }
        int lastIndexOf = spec.lastIndexOf('(');
        if (lastIndexOf != -1) {
            if (isText) {
                textIndex = Integer.parseInt(spec.substring(lastIndexOf + 1, spec.length() - 1));
                text = spec.substring(0, lastIndexOf);
            } else {
                hRefIndex = Integer.parseInt(spec.substring(lastIndexOf + 1, spec.length() - 1));
                hRef = spec.substring(0, lastIndexOf);
            }
        }
        if (text != null) {
            p.setProperty("text", text);
            p.setProperty("textIndex", textIndex + "");
        } else {
            p.setProperty("href", hRef);
            p.setProperty("hRefIndex", hRefIndex + "");
        }
        return p;
    }

    private void fillElements(Tag tag, ArrayList<IJavaElement> r, Predicate predicate) {
        HTMLDocument document = (HTMLDocument) ((JEditorPane) getComponent()).getDocument();
        Iterator iterator = document.getIterator(tag);
        int index = 0;
        while (iterator.isValid()) {
            JEditorPaneTagJavaElement e = new JEditorPaneTagJavaElement(this, tag, index++);
            if (predicate.isValid(e)) {
                r.add(e);
            }
            iterator.next();
        }
    }

    private static final Tag allTags[] = { HTML.Tag.A, HTML.Tag.ADDRESS, HTML.Tag.APPLET, HTML.Tag.AREA, HTML.Tag.B, HTML.Tag.BASE,
            HTML.Tag.BASEFONT, HTML.Tag.BIG, HTML.Tag.BLOCKQUOTE, HTML.Tag.BODY, HTML.Tag.BR, HTML.Tag.CAPTION, HTML.Tag.CENTER,
            HTML.Tag.CITE, HTML.Tag.CODE, HTML.Tag.DD, HTML.Tag.DFN, HTML.Tag.DIR, HTML.Tag.DIV, HTML.Tag.DL, HTML.Tag.DT,
            HTML.Tag.EM, HTML.Tag.FONT, HTML.Tag.FORM, HTML.Tag.FRAME, HTML.Tag.FRAMESET, HTML.Tag.H1, HTML.Tag.H2, HTML.Tag.H3,
            HTML.Tag.H4, HTML.Tag.H5, HTML.Tag.H6, HTML.Tag.HEAD, HTML.Tag.HR, HTML.Tag.HTML, HTML.Tag.I, HTML.Tag.IMG,
            HTML.Tag.INPUT, HTML.Tag.ISINDEX, HTML.Tag.KBD, HTML.Tag.LI, HTML.Tag.LINK, HTML.Tag.MAP, HTML.Tag.MENU, HTML.Tag.META,
            HTML.Tag.NOFRAMES, HTML.Tag.OBJECT, HTML.Tag.OL, HTML.Tag.OPTION, HTML.Tag.P, HTML.Tag.PARAM, HTML.Tag.PRE,
            HTML.Tag.SAMP, HTML.Tag.SCRIPT, HTML.Tag.SELECT, HTML.Tag.SMALL, HTML.Tag.SPAN, HTML.Tag.STRIKE, HTML.Tag.S,
            HTML.Tag.STRONG, HTML.Tag.STYLE, HTML.Tag.SUB, HTML.Tag.SUP, HTML.Tag.TABLE, HTML.Tag.TD, HTML.Tag.TEXTAREA,
            HTML.Tag.TH, HTML.Tag.TITLE, HTML.Tag.TR, HTML.Tag.TT, HTML.Tag.U, HTML.Tag.UL, HTML.Tag.VAR };

    private Tag findTag(String tagName) {
        for (Tag tag : allTags) {
            if (tagName.toUpperCase().equals(tag.toString().toUpperCase())) {
                return tag;
            }
        }
        return null;
    }

}
