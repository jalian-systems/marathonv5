package net.sourceforge.marathon.javafxagent.components;

import java.util.Properties;

import javax.swing.SwingUtilities;

import org.json.JSONObject;

import net.sourceforge.marathon.javafxagent.IJavaElement;

public class JavaElementTest {

    protected IJavaElement marathon_select_by_properties(IJavaElement e, String select, boolean isEdit) {
        Properties p = new Properties();
        p.setProperty("select", select);
        JSONObject o = new JSONObject(p);
        String encodedState = "select-by-properties('" + o.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")
                + "')";
        return e.findElementByCssSelector(".::" + encodedState + (isEdit ? "::editor" : ""));
    }

    protected IJavaElement marathon_select_by_properties(IJavaElement e, Properties p, boolean isEdit) {
        JSONObject o = new JSONObject(p);
        String encodedState = "select-by-properties('" + o.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")
                + "')";
        return e.findElementByCssSelector(".::" + encodedState + (isEdit ? "::editor" : ""));
    }

    protected void marathon_select(IJavaElement e, String state) {
        String encodedState = state.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'");
        e.findElementByCssSelector(".::call-select('" + encodedState + "')");
    }

    public void siw(Runnable doRun) {
        try {
            SwingUtilities.invokeAndWait(doRun);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
