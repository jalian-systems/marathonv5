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
package net.sourceforge.marathon.javafxrecorder.component;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.event.ChangeEvent;

import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.marathon.javafxagent.JavaFXElementPropertyAccessor;
import net.sourceforge.marathon.javafxagent.components.ContextManager;
import net.sourceforge.marathon.javafxrecorder.IJSONRecorder;
import net.sourceforge.marathon.javafxrecorder.JSONOMapConfig;

public abstract class RFXComponent extends JavaFXElementPropertyAccessor {

    public static final Logger LOGGER = Logger.getLogger(RFXComponent.class.getName());

    protected IJSONRecorder recorder;
    protected JSONOMapConfig omapConfig;
    private RFXComponentFactory finder;

    public RFXComponent(Node source, JSONOMapConfig omapConfig, Point2D point, IJSONRecorder recorder) {
        super(source);
        this.omapConfig = omapConfig;
        this.recorder = recorder;
    }

    public void processEvent(Event event) {
        if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;

            EventType<? extends MouseEvent> eventType = me.getEventType();
            if (eventType == MouseEvent.MOUSE_ENTERED) {
                mouseEntered(me);
            } else if (eventType == MouseEvent.MOUSE_PRESSED) {
                mousePressed(me);
            } else if (eventType == MouseEvent.MOUSE_RELEASED) {
                mouseReleased(me);
            } else if (eventType == MouseEvent.MOUSE_CLICKED) {
                mouseClicked(me);
            } else if (eventType == MouseEvent.MOUSE_EXITED) {
                mouseExited(me);
            }
        } else if (event instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) event;
            EventType<KeyEvent> eventType = ke.getEventType();
            if (eventType == KeyEvent.KEY_PRESSED) {
                keyPressed(ke);
            } else if (eventType == KeyEvent.KEY_RELEASED) {
                keyReleased(ke);
            } else if (eventType == KeyEvent.KEY_TYPED) {
                keyTyped(ke);
            }
        }
    }

    public void handleRawRecording(IJSONRecorder recorder, Event event) {
        if (event instanceof MouseEvent && event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            recorder.recordRawMouseEvent(this, (MouseEvent) event);
        }
        if (event instanceof KeyEvent && event.getEventType() != KeyEvent.KEY_RELEASED) {
            recorder.recordRawKeyEvent(this, (KeyEvent) event);
        }
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (node == null ? 0 : node.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RFXComponent other = (RFXComponent) obj;
        if (node == null) {
            if (other.node != null) {
                return false;
            }
        } else if (!node.equals(other.node)) {
            return false;
        }
        return true;
    }

    public Map<String, String> findURP() {
        return findURP(omapConfig.findRP(node.getClass()));
    }

    public Map<String, String> findAttributes() {
        return findAttributes(omapConfig.findProperties());
    }

    public JSONObject findContextHeirarchy() {
        return findContextHeirarchy(node.getParent());
    }

    public JSONObject findContextHeirarchy(Parent parent) {
        JSONObject r = null;
        JSONObject current = null;
        while (parent != null) {
            if (ContextManager.isContext(parent)) {
                JSONObject pContext = getContextJSONObject(parent);
                if (r == null) {
                    r = pContext;
                }
                if (current != null) {
                    current.put("container", pContext);
                }
                current = pContext;
            }
            parent = parent.getParent();
        }
        return addWindowParents(r, current);
    }

    private JSONObject addWindowParents(JSONObject r, JSONObject current) {
        Stage stage = getStage(node.getScene().getWindow());
        while (stage != null) {
            JSONObject pWindow = getContextJSONObject(stage);
            if (r == null) {
                r = pWindow;
            }
            if (current != null) {
                current.put("container", pWindow);
            }
            current = pWindow;
            stage = getStage(stage.getOwner());
        }
        return r;
    }

    private JSONObject getContextJSONObject(Stage stage) {
        Collection<String> properties = omapConfig.findProperties();
        Map<String, String> attributes = new HashMap<String, String>();
        FXStagePropertyAccessor pa = new FXStagePropertyAccessor(stage);
        for (String prop : properties) {
            String value = pa.getAttribute(prop);
            attributes.put(prop, value);
        }
        JSONObject r = new JSONObject();
        r.put("attributes", attributes);
        List<List<String>> rp = omapConfig.findContainerRP(stage.getClass());
        r.put("containerURP", pa.findURP(rp));
        List<List<String>> np = omapConfig.findContainerNP(stage.getClass());
        Map<String, String> urp = pa.findURP(np);
        r.put("container_type", "window");
        r.put("urp", urp);
        return r;
    }

    private Stage getStage(Window window) {
        if (window instanceof Stage) {
            return (Stage) window;
        }
        if (window instanceof PopupWindow) {
            Node ownerNode = ((PopupWindow) window).getOwnerNode();
            ownerNode.getScene().getWindow();
            return getStage(ownerNode.getScene().getWindow());
        }
        return null;
    }

    private JSONObject getContextJSONObject(Parent parent) {
        RFXComponentFactory finder = getFinder();
        RFXComponent pa = finder.findRComponent(parent, null, recorder);
        Collection<String> properties = omapConfig.findProperties();
        Map<String, String> attributes = new HashMap<String, String>();
        for (String prop : properties) {
            String value = pa.getAttribute(prop);
            attributes.put(prop, value);
        }
        JSONObject r = new JSONObject();
        r.put("attributes", attributes);
        if (parent == null) {
            throw new RuntimeException("parent == null for " + node);
        }
        List<List<String>> rp = omapConfig.findContainerRP(parent.getClass());
        r.put("containerURP", pa.findURP(rp));
        List<List<String>> np = omapConfig.findContainerNP(parent.getClass());
        Map<String, String> urp = pa.findURP(np);
        r.put("container_type", "frame");
        r.put("urp", urp);
        return r;
    }

    public String getCellInfo() {
        return "";
    }

    public String getRComponentName() {
        return findURP().toString();
    }

    public ArrayList<Method> getMethods() {
        // @formatter:off
		String[] methods = new String[] { "isEnabled", "getRowCount", "getColumnCount", "getItemCount", "getModelSize",
				"getInstanceOf", "getFieldName", "getFieldNames", "getTooltipText",
				"getAccessibleName", "getId" };
		// @formatter:on
        ArrayList<Method> l = new ArrayList<Method>();
        if (getText() != null) {
            addMethod(l, "getText");
        }
        if (getContent() != null) {
            addMethod(l, "getContent");
        }
        if (getLabeledBy() != null) {
            addMethod(l, "getLabeledBy");
        }
        Arrays.sort(methods, new Comparator<String>() {
            @Override public int compare(String o1, String o2) {
                if (o1.startsWith("is")) {
                    o1 = o1.substring(2);
                } else if (o1.startsWith("get")) {
                    o1 = o1.substring(3);
                }
                if (o2.startsWith("is")) {
                    o2 = o2.substring(2);
                } else if (o2.startsWith("get")) {
                    o2 = o2.substring(3);
                }
                return o1.compareTo(o2);
            }
        });
        for (String n : methods) {
            addMethod(l, n);
        }
        addMethod(l, "getComponent");
        return l;
    }

    protected void addMethod(ArrayList<Method> l, String name) {
        try {
            Method method = this.getClass().getMethod(name, new Class[] {});
            Object r = method.invoke(this);
            if (r == null || "".equals(r) || r.getClass().isArray() && Array.getLength(r) == 0
                    || r instanceof List && ((List<?>) r).size() == 0) {
                return;
            }
            l.add(method);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    public String[][] getContent() {
        return null;
    }

    public static Object unboxPremitiveArray(Object r) {
        int length = Array.getLength(r);
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < length; i++) {
            Object e = Array.get(r, i);
            if (e != null && e.getClass().isArray()) {
                list.add(unboxPremitiveArray(e));
            } else {
                list.add(e);
            }
        }
        return list;
    }

    protected void mouseExited(MouseEvent me) {
    }

    protected void mouseClicked(MouseEvent me) {
    }

    protected void mouseReleased(MouseEvent me) {
    }

    protected void mousePressed(MouseEvent me) {
        if (me.isPrimaryButtonDown() && me.getClickCount() == 1 && !me.isAltDown() && !me.isMetaDown() && !me.isControlDown()) {
            mouseButton1Pressed(me);
        } else {
            recorder.recordClick2(this, me, true);
        }
    }

    protected void mouseButton1Pressed(MouseEvent me) {
    }

    protected void mouseEntered(MouseEvent me) {
    }

    protected void keyTyped(KeyEvent ke) {
    }

    protected void keyReleased(KeyEvent ke) {
    }

    protected void keyPressed(KeyEvent ke) {
    }

    public void focusLost(RFXComponent next) {
    }

    public void focusGained(RFXComponent prev) {
    }

    public void stateChanged(ChangeEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
    }

    public boolean isMenuShortcutKeyDown(MouseEvent event) {
        return event.isShortcutDown();
    }

    protected RFXComponentFactory getFinder() {
        if (finder == null) {
            finder = new RFXComponentFactory(omapConfig);
        }
        return finder;
    }

}
