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

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.Iterator;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.demos.editorpane.EditorPaneDemo;

import net.sourceforge.marathon.component.LoggingRecorder.Call;
import net.sourceforge.marathon.javaagent.Device;
import net.sourceforge.marathon.javaagent.EventQueueWait;
import net.sourceforge.marathon.javaagent.IDevice;
import net.sourceforge.marathon.javaagent.IDevice.Buttons;
import net.sourceforge.marathon.javaagent.Wait;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

@Test public class REditorPaneTest extends RComponentTest {
    private JFrame frame;
    private int linkPosition;
    @SuppressWarnings("unused") private String hRef;
    @SuppressWarnings("unused") private String text;
    private char SEPARATER = ',';
    private int hRefIndex;
    private int textIndex;
    private Rectangle rect;
    protected Point p;
    private JEditorPane editor;

    public JEditorPane getEditor() {
        return editor;
    }

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override public void run() {
                frame = new JFrame(REditorPaneTest.class.getSimpleName());
                frame.setName("frame-" + REditorPaneTest.class.getSimpleName());
                frame.getContentPane().add(new EditorPaneDemo(), BorderLayout.CENTER);
                frame.setSize(800, 600);
                frame.setVisible(true);
            }
        });
        editor = (JEditorPane) ComponentUtils.findComponent(JEditorPane.class, frame);
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    public void clickOnText() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                REditorPane rEditorPane = new REditorPane(editor, null, e.getPoint(), lr);
                rEditorPane.mouseButton1Pressed(e);
            }
        });
        parseLastClickSpec("text=Title Page");
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        new Wait("Waiting for logging recorder callback") {
            @Override public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };
        AssertJUnit.assertEquals("click", lr.getCall().getFunction());
        AssertJUnit.assertEquals("text=Title Page", lr.getCall().getCellinfo());
    }

    public void clickOnTextDuplicate() throws Throwable {
        final String htmlText = "<a href='http://localhost'>Goto Google</a>Some stuff here<a href='http://localhost'>Goto Google</a>";
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                editor.setText(htmlText);
            }
        });
        EventQueueWait.empty();
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                REditorPane rEditorPane = new REditorPane(editor, null, e.getPoint(), lr);
                rEditorPane.mouseButton1Pressed(e);
            }
        });
        parseLastClickSpec("text=Goto Google(1)");
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        new Wait("Waiting for logging recorder callback") {
            @Override public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };
        AssertJUnit.assertEquals("click", lr.getCall().getFunction());
        AssertJUnit.assertEquals("text=Goto Google(1)", lr.getCall().getCellinfo());
    }

    public void clickOnLinkDuplicate() throws Throwable {
        final String htmlText = "<a href='http://localhost'><img src='none.gif'></a>" + "Some stuff here"
                + "<a href='http://localhost'><img src='none.gif'></a>";
        final LoggingRecorder lr = new LoggingRecorder();
        siw(new Runnable() {
            @Override public void run() {
                editor.setText(htmlText);
            }
        });
        EventQueueWait.empty();
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                REditorPane rEditorPane = new REditorPane(editor, null, e.getPoint(), lr);
                rEditorPane.mouseButton1Pressed(e);
            }
        });
        parseLastClickSpec("link=http://localhost(1)");
        System.out.println("LinkPosition: " + linkPosition);
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        new Wait("Waiting for logging recorder callback") {
            @Override public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };
        AssertJUnit.assertEquals("click", lr.getCall().getFunction());
        AssertJUnit.assertEquals("link=http://localhost(1)", lr.getCall().getCellinfo());
    }

    public void clickOnLink() throws Throwable {
        parseLastClickSpec("text=Title Page");
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        parseLastClickSpec("link=index.html");
        final LoggingRecorder lr = new LoggingRecorder();
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                REditorPane rEditorPane = new REditorPane(editor, null, e.getPoint(), lr);
                rEditorPane.mouseButton1Pressed(e);
            }
        });
        parseLastClickSpec("link=index.html");
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        new Wait("Waiting for logging recorder callback") {
            @Override public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };
        Call call = lr.getCall();
        AssertJUnit.assertEquals("click", call.getFunction());
        AssertJUnit.assertEquals("link=index.html", call.getCellinfo());
    }

    public void clickOnEditorPane() throws Throwable {
        final LoggingRecorder lr = new LoggingRecorder();
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                REditorPane rEditorPane = new REditorPane(editor, null, e.getPoint(), lr);
                rEditorPane.mouseButton1Pressed(e);
            }
        });
        parseLastClickSpec("953");
        siw(new Runnable() {
            @Override public void run() {
                try {
                    rect = editor.modelToView(linkPosition);
                    p = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                } catch (BadLocationException e) {
                    throw new RuntimeException("BadLocation: " + linkPosition, e);
                }
                IDevice d = Device.getDevice();
                d.click(editor, Buttons.LEFT, 1, p.x, p.y);
            }
        });
        new Wait("Waiting for logging recorder callback") {
            @Override public boolean until() {
                return lr.getCalls().size() > 0;
            }
        };
        AssertJUnit.assertEquals("click", lr.getCall().getFunction());
        AssertJUnit.assertEquals("953", lr.getCall().getCellinfo());
    }

    private void parseLastClickSpec(final String spec) {
        new Wait("Waiting for the spec `" + spec + "` to be available") {
            @Override public boolean until() {
                siw(new Runnable() {
                    @Override public void run() {
                        parseLastClickSpecX(spec);
                    }
                });
                return linkPosition != -1;
            }
        };
    }

    private void parseLastClickSpecX(String spec) {
        if (spec.startsWith("text=")) {
            searchAsText(spec.substring(5), true);
        } else if (spec.startsWith("link=")) {
            searchAsText(spec.substring(5), false);
        } else {
            try {
                int index = spec.lastIndexOf(SEPARATER);
                if (index >= 0) {
                    hRef = spec.substring(0, index).trim();
                    spec = spec.substring(index + 1).trim();
                }
                linkPosition = Integer.parseInt(spec);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void searchAsText(String spec, boolean isText) {
        Document document = getEditor().getDocument();
        hRef = null;
        text = null;
        hRefIndex = 0;
        textIndex = 0;
        linkPosition = -1;
        int lastIndexOf = spec.lastIndexOf('(');
        if (lastIndexOf != -1) {
            if (isText) {
                textIndex = Integer.parseInt(spec.substring(lastIndexOf + 1, spec.length() - 1));
            } else {
                hRefIndex = Integer.parseInt(spec.substring(lastIndexOf + 1, spec.length() - 1));
            }
            spec = spec.substring(0, lastIndexOf);
        }
        if (!(document instanceof HTMLDocument)) {
            return;
        }
        HTMLDocument hdoc = (HTMLDocument) document;
        Iterator iterator = hdoc.getIterator(HTML.Tag.A);
        int curIndex = 0;
        while (iterator.isValid()) {
            String t;
            AttributeSet attributes = iterator.getAttributes();
            try {
                if (isText) {
                    t = hdoc.getText(iterator.getStartOffset(), iterator.getEndOffset() - iterator.getStartOffset());
                } else {
                    t = attributes.getAttribute(HTML.Attribute.HREF).toString();
                }
            } catch (BadLocationException e1) {
                return;
            }
            if (t.contains(spec) && (isText && curIndex++ == textIndex || !isText && curIndex++ == hRefIndex)) {
                if (attributes != null && attributes.getAttribute(HTML.Attribute.HREF) != null) {
                    try {
                        text = hdoc.getText(iterator.getStartOffset(), iterator.getEndOffset() - iterator.getStartOffset()).trim();
                        hRef = attributes.getAttribute(HTML.Attribute.HREF).toString();
                        linkPosition = (iterator.getStartOffset() + iterator.getEndOffset()) / 2;
                    } catch (BadLocationException e) {
                        return;
                    }
                    return;
                }
            }
            iterator.next();
        }
    }
}
