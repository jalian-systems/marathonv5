package net.sourceforge.marathon.runtime;

import java.awt.event.MouseEvent;

import org.json.JSONObject;

import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.Indent;
import net.sourceforge.marathon.runtime.api.ScriptModel;
import net.sourceforge.marathon.runtime.api.WindowId;

public class JSONScriptElement implements IScriptElement {
    private final WindowId windowId;
    private final JSONObject event;
    private final String name;
    private static final long serialVersionUID = 1L;

    public JSONScriptElement(WindowId windowId, String name, JSONObject event) {
        this.windowId = windowId;
        this.event = event;
        this.name = name;
    }

    @Override public String toScriptCode() {
        if (event.getString("type").equals("key_raw"))
            return enscriptKeystroke();
        else if (event.getString("type").equals("click_raw"))
            return enscriptRawMouseClick();
        else if (event.getString("type").equals("click"))
            return enscriptMouseClick();
        else if (event.getString("type").equals("select"))
            return enscriptSelect();
        else if (event.getString("type").equals("select_menu"))
            return enscriptSelectMenu();
        else if (event.getString("type").equals("menu_item"))
            return enscriptMenuItem();
        else if (event.getString("type").equals("assert") || event.getString("type").equals("wait"))
            return enscriptAssert(event.getString("type"));
        else if (event.getString("type").equals("window_closed"))
            return enscriptWindowClosed();
        else if (event.getString("type").equals("window_state"))
            return enscriptWindowState();
        return "on '" + name + "' " + event.toString(4);
    }

    private String enscriptWindowClosed() {
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("window_closed", name);
    }

    private String enscriptWindowState() {
        return Indent.getIndent()
                + ScriptModel.getModel().getScriptCodeForGenericAction("window_changed", event.getString("bounds"));
    }

    private String enscriptAssert(String type) {
        String property = event.getString("property");
        String cellinfo = null;
        if (event.has("cellinfo"))
            cellinfo = event.getString("cellinfo");
        String method = "assert_p";
        if (type.equals("wait"))
            method = "wait_p";
        else if (property.equalsIgnoreCase("content"))
            method = "assert_content";
        if (method.equals("assert_content"))
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction(method, name, event.get("value"));
        if (cellinfo == null || "".equals(cellinfo))
            return Indent.getIndent()
                    + ScriptModel.getModel().getScriptCodeForGenericAction(method, name, property, event.get("value"));
        else
            return Indent.getIndent()
                    + ScriptModel.getModel().getScriptCodeForGenericAction(method, name, property, event.get("value"), cellinfo);
    }

    private String enscriptSelect() {
        String value = event.getString("value");
        String cellinfo = null;
        if (event.has("cellinfo"))
            cellinfo = event.getString("cellinfo");
        if (cellinfo == null)
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("select", name, value);
        else
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("select", name, value, cellinfo);
    }

    private String enscriptSelectMenu() {
        String value = event.getString("value");
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("select_menu", value);
    }

    private String enscriptMenuItem() {
        return "";
    }

    private String enscriptRawMouseClick() {
        boolean popupTrigger = event.getInt("button") == MouseEvent.BUTTON3;
        int clickCount = event.getInt("clickCount");
        int x = event.getInt("x");
        int y = event.getInt("y");
        String mtext = event.getString("modifiersEx");
        String method = "click";
        if (popupTrigger)
            method = "rightclick";
        if ("".equals(mtext))
            return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction(method, name, clickCount, x, y);
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction(method, name, clickCount, x, y, mtext);
    }

    private String enscriptMouseClick() {
        boolean popupTrigger = event.getInt("button") == MouseEvent.BUTTON3;
        int clickCount = event.has("clickCount") ? event.getInt("clickCount") : 1;
        String mtext = event.getString("modifiersEx");
        String cellinfo = null;
        if (event.has("cellinfo"))
            cellinfo = event.getString("cellinfo");
        if ("".equals(cellinfo))
            cellinfo = null;
        if (popupTrigger) {
            if (clickCount == 1) {
                if ("".equals(mtext)) {
                    if (cellinfo == null)
                        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name);
                    return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, cellinfo);
                } else {
                    if (cellinfo == null)
                        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, mtext);
                    return Indent.getIndent()
                            + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, mtext, cellinfo);
                }
            } else {
                if ("".equals(mtext)) {
                    if (cellinfo == null)
                        return Indent.getIndent()
                                + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, clickCount);
                    return Indent.getIndent()
                            + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, clickCount, cellinfo);
                } else {
                    if (cellinfo == null)
                        return Indent.getIndent()
                                + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, clickCount, mtext);
                    return Indent.getIndent()
                            + ScriptModel.getModel().getScriptCodeForGenericAction("rightclick", name, clickCount, mtext, cellinfo);
                }
            }
        } else {
            if (clickCount == 1) {
                if ("".equals(mtext)) {
                    if (cellinfo == null)
                        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("click", name);
                    return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("click", name, cellinfo);
                } else {
                    if (cellinfo == null)
                        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("click", name, mtext);
                    return Indent.getIndent()
                            + ScriptModel.getModel().getScriptCodeForGenericAction("click", name, mtext, cellinfo);
                }
            } else {
                if ("".equals(mtext)) {
                    if (cellinfo == null)
                        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", name);
                    return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", name, cellinfo);
                } else {
                    if (cellinfo == null)
                        return Indent.getIndent()
                                + ScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", name, mtext);
                    return Indent.getIndent()
                            + ScriptModel.getModel().getScriptCodeForGenericAction("doubleclick", name, mtext, cellinfo);
                }
            }
        }
    }

    private String enscriptKeystroke() {
        String keytext = null;
        if (event.has("keyChar")) {
            keytext = event.getString("keyChar");
        } else {
            String mtext = event.getString("modifiersEx");
            if (mtext.length() > 0)
                mtext = mtext + "+";
            keytext = mtext + event.getString("keyCode");
        }
        return Indent.getIndent() + ScriptModel.getModel().getScriptCodeForGenericAction("keystroke", name, keytext);
    }

    @Override public WindowId getWindowId() {
        return windowId;
    }

    @Override public IScriptElement getUndoElement() {
        return null;
    }

    @Override public boolean isUndo() {
        return false;
    }

    @Override public String toString() {
        return "JSONScriptElement [windowId=" + windowId + ", event=" + event + ", name=" + name + "]";
    }

}
