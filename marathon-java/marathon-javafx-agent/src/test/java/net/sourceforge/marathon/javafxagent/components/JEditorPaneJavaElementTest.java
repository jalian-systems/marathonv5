package net.sourceforge.marathon.javafxagent.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;

import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;
import net.sourceforge.marathon.javafxagent.JavaAgent;
import net.sourceforge.marathon.javafxagent.JavaElementFactory;
import net.sourceforge.marathon.javafxagent.Wait;
import net.sourceforge.marathon.javafxagent.components.JEditorPaneJavaElement;
import net.sourceforge.marathon.javafxagent.components.JavaElementTest;
import net.sourceforge.marathon.testhelpers.ComponentUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sun.swingset3.demos.editorpane.EditorPaneDemo;

@Test public class JEditorPaneJavaElementTest extends JavaElementTest {
    protected JFrame frame;
    private IJavaAgent driver;
    private IJavaElement editor;

    @BeforeMethod public void showDialog() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override public void run() {
                frame = new JFrame(JEditorPaneJavaElementTest.class.getSimpleName());
                frame.setName("frame-" + JEditorPaneJavaElementTest.class.getSimpleName());

                EditorPaneDemo editorPane = new EditorPaneDemo();
                editorPane.setName("EditorPane");
                frame.getContentPane().add(editorPane, BorderLayout.CENTER);
                frame.setSize(640, 600);
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
            }
        });
        driver = new JavaAgent();
        JavaElementFactory.add(JEditorPane.class, JEditorPaneJavaElement.class);
        editor = driver.findElementByTagName("editor-pane");
        new Wait("Waiting for document to load") {
            @Override public boolean until() {
                try {
                    return getFileName() != null;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        Thread.sleep(500);
    }

    @AfterMethod public void disposeDriver() throws Throwable {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        JavaElementFactory.reset();
    }

    @Test(enabled = false) public void clickOnText() throws Throwable {
        AssertJUnit.assertEquals("index.html", getFileName());
        new Wait("Waiting for element to be available") {
            @Override public boolean until() {
                try {
                    marathon_select_by_properties(editor, "text=Of a Louse", false);
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        IJavaElement prop = marathon_select_by_properties(editor, "text=Of a Louse", false);
        prop.click();
        new Wait("Waiting for document to load") {
            @Override public boolean until() {
                try {
                    return getFileName().equals("bug.html");
                } catch (Throwable t) {
                }
                return false;
            }
        };
        AssertJUnit.assertEquals("bug.html", getFileName());
    }

    public void clickOnLink() throws Throwable {
        AssertJUnit.assertEquals("index.html", getFileName());
        new Wait("Waiting for element to be available") {
            @Override public boolean until() {
                try {
                    marathon_select_by_properties(editor, "link=bug.html", false);
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        IJavaElement prop = marathon_select_by_properties(editor, "link=bug.html", false);
        prop.click();
        new Wait("Waiting for document to load") {
            @Override public boolean until() {
                try {
                    return getFileName().equals("bug.html");
                } catch (Throwable t) {
                }
                return false;
            }
        };
        AssertJUnit.assertEquals("bug.html", getFileName());
    }

    public void clickOnLinkWithIndex() throws Throwable {
        final String htmlText = "<a href='http://localhost' id='#first'><img src='none.gif'></a>" + "Some stuff here"
                + "<a href='http://localhost' id='#second'><img src='none.gif'></a>";
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                JEditorPane editor = (JEditorPane) ComponentUtils.findComponent(JEditorPane.class, frame);
                Document d = editor.getEditorKit().createDefaultDocument();
                editor.setDocument(d);
                editor.setText(htmlText);
            }
        });
        new Wait("Waiting for element to be available") {
            @Override public boolean until() {
                try {
                    marathon_select_by_properties(editor, "link=http://localhost(1)", false);
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        IJavaElement prop = marathon_select_by_properties(editor, "link=http://localhost(1)", false);
        AssertJUnit.assertEquals("#second", prop.getAttribute("id"));
    }

    public void clickOnTextWithIndex() throws Throwable {
        final String htmlText = "<a href='http://localhost' id='#first'>Goto Google</a>" + "Some stuff here"
                + "<a href='http://localhost' id='#second'>Goto Google</a>";
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                JEditorPane editor = (JEditorPane) ComponentUtils.findComponent(JEditorPane.class, frame);
                Document d = editor.getEditorKit().createDefaultDocument();
                editor.setDocument(d);
                editor.setText(htmlText);
            }
        });
        new Wait("Waiting for element to be available") {
            @Override public boolean until() {
                try {
                    marathon_select_by_properties(editor, "text=Goto Google(1)", false);
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        IJavaElement prop = marathon_select_by_properties(editor, "text=Goto Google(1)", false);
        AssertJUnit.assertEquals("#second", prop.getAttribute("id"));
    }

    protected String getFileName() throws InterruptedException, InvocationTargetException {
        final String[] values = new String[] { null };
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                JEditorPane editor = (JEditorPane) ComponentUtils.findComponent(JEditorPane.class, frame);
                HTMLDocument document = (HTMLDocument) editor.getDocument();
                URL base = document.getBase();
                if (base != null) {
                    String path = base.toString();
                    path = path.substring(path.lastIndexOf('/') + 1);
                    values[0] = path;
                }
            }
        });
        String path = values[0];
        return path;
    }

    public void clickByPos() throws Throwable {
        AssertJUnit.assertEquals("index.html", getFileName());
        final Point expected = new Point();
        final Point actual = new Point();
        final JEditorPane editorPane = (JEditorPane) ComponentUtils.findComponent(JEditorPane.class, frame);
        editorPane.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                actual.x = e.getPoint().x;
                actual.y = e.getPoint().y;
            }
        });
        new Wait("Waiting for element to be available") {
            @Override public boolean until() {
                try {
                    marathon_select_by_properties(editor, "353", false);
                    return true;
                } catch (Throwable t) {
                }
                return false;
            }
        };
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                try {
                    Rectangle bounds = editorPane.modelToView(353);
                    Point location = new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
                    expected.x = location.x;
                    expected.y = location.y;
                } catch (BadLocationException e) {
                    throw new RuntimeException("Failed to locate", e);
                }
            }
        });
        IJavaElement prop = marathon_select_by_properties(editor, "353", false);
        prop.click();
        AssertJUnit.assertEquals(expected.x, actual.x);
        AssertJUnit.assertEquals(expected.y, actual.y);
    }

}
