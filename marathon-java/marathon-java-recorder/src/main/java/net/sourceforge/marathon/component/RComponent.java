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

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;

import org.json.JSONObject;

import net.sourceforge.marathon.javaagent.JavaElementPropertyAccessor;
import net.sourceforge.marathon.javaagent.components.ContextManager;
import net.sourceforge.marathon.javarecorder.IJSONRecorder;
import net.sourceforge.marathon.javarecorder.JSONOMapConfig;

public abstract class RComponent extends JavaElementPropertyAccessor {

    public static final Logger LOGGER = Logger.getLogger(RComponent.class.getName());

    protected IJSONRecorder recorder;
    protected JSONOMapConfig omapConfig;

    public RComponent(Component source, JSONOMapConfig omapConfig, Point point, IJSONRecorder recorder) {
        super(source);
        this.omapConfig = omapConfig;
        this.recorder = recorder;
    }

    public void processEvent(AWTEvent event) {
        setIndexOfType(super.getIndexOfType());
        if (event instanceof MouseEvent) {
            MouseEvent me = (MouseEvent) event;

            switch (me.getID()) {
            case MouseEvent.MOUSE_ENTERED:
                mouseEntered(me);
                break;
            case MouseEvent.MOUSE_PRESSED:
                mousePressed(me);
                break;
            case MouseEvent.MOUSE_RELEASED:
                mouseReleased(me);
                break;
            case MouseEvent.MOUSE_CLICKED:
                mouseClicked(me);
                break;
            case MouseEvent.MOUSE_EXITED:
                mouseExited(me);
                break;
            }
        } else if (event instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) event;
            switch (ke.getID()) {
            case KeyEvent.KEY_PRESSED:
                keyPressed(ke);
                break;
            case KeyEvent.KEY_RELEASED:
                keyReleased(ke);
                break;
            case KeyEvent.KEY_TYPED:
                keyTyped(ke);
                break;
            }
        }
    }

    public void handleRawRecording(IJSONRecorder recorder, AWTEvent event) {
        if (event instanceof MouseEvent && event.getID() == MouseEvent.MOUSE_PRESSED) {
            recorder.recordRawMouseEvent(this, (MouseEvent) event);
        }
        if (event instanceof KeyEvent && event.getID() != KeyEvent.KEY_RELEASED) {
            recorder.recordRawKeyEvent(this, (KeyEvent) event);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (component == null ? 0 : component.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RComponent other = (RComponent) obj;
        if (component == null) {
            if (other.component != null) {
                return false;
            }
        } else if (!component.equals(other.component)) {
            return false;
        }
        return true;
    }

    public Map<String, String> findURP() {
        return findURP(omapConfig.findRP(component.getClass()));
    }

    public Map<String, String> findAttributes() {
        return findAttributes(omapConfig.findProperties());
    }

    public List<List<String>> getNamingProperties() {
        return omapConfig.findNP(component.getClass());
    }

    public JSONObject findContextHeirarchy() {
        return findContextHeirarchy(component.getParent());
    }

    public JSONObject findContextHeirarchy(Container parent) {
        JSONObject r = null;
        JSONObject current = null;
        while (parent != null && !(parent instanceof Window)) {
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
        return addWindowParents(r, (Window) parent, current);
    }

    private JSONObject addWindowParents(JSONObject r, Window parent, JSONObject current) {
        while (parent != null) {
            if (!parent.getClass().getName().equals("javax.swing.SwingUtilities$SharedOwnerFrame")
                    && !parent.getClass().getName().equals("javax.swing.Popup$HeavyWeightWindow") && parent.isVisible()) {
                JSONObject pWindow = getContextJSONObject(parent);
                if (r == null) {
                    r = pWindow;
                }
                if (current != null) {
                    current.put("container", pWindow);
                }
                current = pWindow;
            }
            parent = parent.getOwner();
        }
        return r;
    }

    private JSONObject getContextJSONObject(Component parent) {
        RComponentFactory finder = new RComponentFactory(omapConfig);
        RComponent pa = finder.findRComponent(parent, null, recorder);
        Collection<String> properties = omapConfig.findProperties();
        Map<String, String> attributes = new HashMap<String, String>();
        for (String prop : properties) {
            String value = pa.getAttribute(prop);
            attributes.put(prop, value);
        }
        JSONObject r = new JSONObject();
        r.put("attributes", attributes);
        if (parent == null) {
            throw new RuntimeException("parent == null for " + component);
        }
        List<List<String>> rp = omapConfig.findContainerRP(parent.getClass());
        r.put("containerURP", pa.findURP(rp));
        List<List<String>> np = omapConfig.findContainerNP(parent.getClass());
        r.put("urp", pa.findURP(np));
        r.put("container_type", parent instanceof Window ? "window" : "frame");
        return r;
    }

    public String getCellInfo() {
        return "";
    }

    public String getRComponentName() {
        return findURP().toString();
    }

    public List<Method> getMethods() {
        // @formatter:off
        String[] methods = new String[] {
                "isEnabled", "getBackground", "getForeground", "getRowCount",
                "getColumnCount", "getItemCount", "getModelSize", "getFont",
                "getFontFamily", "getBorder", "getInstanceOf",
                "getType", "getSize", "getLocation", "getPosition", "getPrecedingLabel",
                "getFieldName", "getFieldNames", "getTooltipText", "getAccessibleName",
                "getLabeledBy", "getLabelText", "getIconFile", "getOMapClassName"
        };
        // @formatter:on
        ArrayList<Method> l = new ArrayList<Method>();
        if (getText() != null) {
            addMethod(l, "getText");
        }
        if (getContent() != null) {
            addMethod(l, "getContent");
        }
        Arrays.sort(methods, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
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

    @Override
    public int getIndexOfType() {
        if (component instanceof JComponent) {
            Integer index = (Integer) ((JComponent) component).getClientProperty("marathon.indexOfType");
            if (index != null) {
                return index;
            }
        }
        return super.getIndexOfType();
    }

    public void setIndexOfType(int indexOfType) {
        if (component instanceof JComponent) {
            ((JComponent) component).putClientProperty("marathon.indexOfType", indexOfType);
        }
    }

    protected void mouseExited(MouseEvent me) {
    }

    protected void mouseClicked(MouseEvent me) {
    }

    protected void mouseReleased(MouseEvent me) {
    }

    protected void mousePressed(MouseEvent me) {
        if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 1 && !me.isAltDown() && !me.isMetaDown()
                && !me.isAltGraphDown() && !me.isControlDown()) {
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

    public void focusLost(RComponent next) {
    }

    public void focusGained(RComponent prev) {
    }

    public void stateChanged(ChangeEvent e) {
    }

    public void actionPerformed(ActionEvent e) {
    }

    public boolean isMenuShortcutKeyDown(InputEvent event) {
        return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
    }
}
