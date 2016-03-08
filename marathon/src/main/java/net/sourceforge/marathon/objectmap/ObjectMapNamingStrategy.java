package net.sourceforge.marathon.objectmap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import net.sourceforge.marathon.api.INamingStrategy;
import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.ObjectIdentity;
import net.sourceforge.marathon.objectmap.ObjectMapConfiguration.PropertyList;
import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.ComponentNotFoundException;
import net.sourceforge.marathon.runtime.api.ILogger;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;
import net.sourceforge.marathon.runtime.api.JSONObjectPropertyAccessor;
import net.sourceforge.marathon.runtime.api.PropertyHelper;
import net.sourceforge.marathon.runtime.api.RuntimeLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class ObjectMapNamingStrategy implements INamingStrategy {

    private static final String MODULE = "Object Map";
    protected ILogger runtimeLogger;
    private IObjectMapService omapService;
    private IPropertyAccessor topContainerAccessor;

    public ObjectMapNamingStrategy() {
        init();
    }

    public void init() {
        runtimeLogger = RuntimeLogger.getRuntimeLogger();
        omapService = getObjectMapService();
        try {
            omapService.load();
            runtimeLogger.info(MODULE, "Loaded object map omapService");
        } catch (IOException e) {
            StringWriter w = new StringWriter();
            e.printStackTrace(new PrintWriter(w));
            runtimeLogger.error(MODULE, "Error in creating naming strategy:" + e.getMessage(), w.toString());
            JOptionPane.showMessageDialog(null, "Error in creating naming strategy:" + e.getMessage(), "Error in NamingStrategy",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setDirty() {
        omapService.setDirty(true);
    }

    protected IObjectMapService getObjectMapService() {
        return new ObjectMapService();
    }

    public void setTopLevelComponent(IPropertyAccessor accessor) {
        topContainerAccessor = accessor;
    }

    private Class<?> findClass(String cName) {
        try {
            return Class.forName(cName);
        } catch (Throwable e) {
            try {
                return Thread.currentThread().getContextClassLoader().loadClass(cName);
            } catch (ClassNotFoundException e1) {
                return JComponent.class;
            }
        }
    }

    private String findCSS(ComponentId id, boolean visibility) throws ObjectMapException {
        String css;
        if (id.getName() != null) {
            OMapComponent findComponentByName = omapService.findComponentByName(id.getName(), topContainerAccessor);
            if (findComponentByName == null)
                throw new NoSuchElementException("No entry found in the object map for `" + id.getName() + "`");
            css = toCSS(findComponentByName, visibility);
        } else {
            css = PropertyHelper.toCSS(id.getNameProps());
        }
        return css;
    }

    private String toCSS(OMapComponent omapComponent, boolean visibility) {
        OMapRecognitionProperty typeProperty = null;
        OMapRecognitionProperty indexProperty = null;
        List<OMapRecognitionProperty> properties = omapComponent.getComponentRecognitionProperties();
        StringBuilder sb = new StringBuilder();
        for (OMapRecognitionProperty rp : properties) {
            if (rp.getName().equals("type")) {
                typeProperty = rp;
            } else if (rp.getName().equals("indexOfType")) {
                indexProperty = rp;
            } else
                sb.append("[").append(rp.getName()).append(op(rp.getMethod())).append("'")
                        .append(rp.getValue().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'")).append("']");
        }
        if (visibility)
            sb.append("[visible='true']");
        String r = sb.toString();
        if (typeProperty != null) {
            r = "[" + typeProperty.getName() + op(typeProperty.getMethod()) + "'" + typeProperty.getValue() + "']" + r;
        }
        if (indexProperty != null) {
            r = r + "[" + indexProperty.getName() + op(indexProperty.getMethod()) + "'" + indexProperty.getValue() + "']";
        }
        return r;
    }

    private Object op(String method) {
        if (method.equals("equals"))
            return "=";
        else if (method.equals("startsWith"))
            return "^=";
        else if (method.equals("endsWith"))
            return "$=";
        else if (method.equals("contains"))
            return "*=";
        else if (method.equals("matches"))
            return "/=";
        throw new RuntimeException("Unknown method " + method + " when converting to CSS");
    }

    public String getName(JSONObject s, String n) throws JSONException, ObjectMapException {
        OMapComponent o = findOMapComponent(s, n);
        if (o != null) {
            o.markEntryNeeded(true);
            return o.getName();
        }
        return null;
    }

    private OMapComponent findOMapComponent(JSONObject component, String n) throws JSONException, ObjectMapException {
        JSONObject window = component.getJSONObject("container");
        Properties urpContainer = PropertyHelper.asProperties(window.getJSONObject("containerURP"));
        Properties attributesContainer = PropertyHelper.asProperties(window.getJSONObject("attributes"));
        List<OMapComponent> omapComponents = omapService.findComponentsByProperties(
                PropertyHelper.asProperties(component.getJSONObject("attributes")), urpContainer, attributesContainer);
        if (omapComponents.size() == 1) {
            return omapComponents.get(0);
        }
        if (omapComponents.size() > 1) {
            String message = "More than one component matched for " + component;
            StringBuilder msg = new StringBuilder(message);
            msg.append("\n    The matched object map entries are:\n");
            for (OMapComponent omc : omapComponents) {
                msg.append("        ").append(omc.toString()).append("\n");
            }
            OMapComponent omapComponent = findClosestMatch(component, omapComponents, msg);
            if (omapComponent != null) {
                runtimeLogger.warning(MODULE, message, msg.toString());
                return omapComponent;
            }
            runtimeLogger.error(MODULE, message, msg.toString());
            throw new ComponentNotFoundException("More than one component matched: " + omapComponents, null);
        }
        String name = createName(component, urpContainer, attributesContainer, n);
        Properties urp = PropertyHelper.asProperties(component.getJSONObject("urp"));
        Properties properties = PropertyHelper.asProperties(component.getJSONObject("attributes"));
        return omapService.insertNameForComponent(name, urp, properties, urpContainer, attributesContainer);
    }

    private String createName(JSONObject component, Properties urpContainer, Properties attributesContainer, String n)
            throws ObjectMapException {
        String name = null;
        if (n == null) {
            IPropertyAccessor w = new JSONObjectPropertyAccessor(component.getJSONObject("attributes"));
            List<List<String>> propertyList = findNamingProperties(w.getProperty("component.class.name"));
            for (List<String> properties : propertyList) {
                name = createName(w, properties);
                if (name == null || name.equals(""))
                    continue;
                if (omapService.findComponentByName(name, urpContainer, attributesContainer) == null)
                    return name;
                break;
            }
        } else
            name = n;
        String original = name;
        int index = 2;
        while (omapService.findComponentByName(name, urpContainer, attributesContainer) != null) {
            name = original + "_" + index++;
        }
        return name;
    }

    private String createName(IPropertyAccessor w, List<String> properties) {
        StringBuilder sb = new StringBuilder();
        for (String property : properties) {
            String v = w.getProperty(property);
            if (v == null || v.equals(""))
                return null;
            sb.append(v).append('_');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString().trim();
    }

    private OMapComponent findClosestMatch(JSONObject component, List<OMapComponent> omapComponents, StringBuilder msg) {
        return null;
    }

    private List<List<String>> findNamingProperties(String cName) {
        List<List<String>> np = findProperties(findClass(cName), omapService.getNamingProperties());
        np.add(OMapComponent.LAST_RESORT_NAMING_PROPERTIES);
        return np;
    }

    private List<List<String>> findProperties(Class<?> class1, List<ObjectIdentity> list) {
        List<PropertyList> selection = new ArrayList<PropertyList>();
        while (class1 != null) {
            for (ObjectIdentity objectIdentity : list) {
                if (objectIdentity.getClassName().equals(class1.getName()))
                    selection.addAll(objectIdentity.getPropertyLists());
            }
            class1 = class1.getSuperclass();
        }
        Collections.sort(selection, new Comparator<PropertyList>() {
            public int compare(PropertyList o1, PropertyList o2) {
                return o2.getPriority() - o1.getPriority();
            }
        });
        List<List<String>> sortedList = new ArrayList<List<String>>();
        for (PropertyList pl : selection) {
            sortedList.add(new ArrayList<String>(pl.getProperties()));
        }
        return sortedList;
    }

    public void save() {
        omapService.save();
    }

    public String getContainerName(JSONObject container) throws JSONException, ObjectMapException {
        // For a container we shall use urp to generate the name
        JSONObject urp = container.getJSONObject("urp");
        StringBuilder sb = new StringBuilder();
        String[] names = JSONObject.getNames(urp);
        for (String name : names) {
            sb.append(urp.get(name).toString()).append(':');
        }
        sb.setLength(sb.length() - 1);
        return getName(container, sb.toString());
    }

    public String getName(JSONObject component) throws JSONException, ObjectMapException {
        return getName(component, null);
    }

    public String[] toCSS(ComponentId componentId, boolean visibility) throws ObjectMapException {
        String[] r = new String[] { null, null };
        r[0] = findCSS(componentId, visibility);
        r[1] = findCSSForInfo(componentId);
        return r;
    }

    private String findCSSForInfo(ComponentId componentId) {
        Properties p;
        if (componentId.getComponentInfo() != null) {
            p = new Properties();
            p.setProperty("select", componentId.getComponentInfo());
        } else if (componentId.getComponentInfoProps() != null) {
            p = componentId.getComponentInfoProps();
        } else {
            return null;
        }
        JSONObject o = new JSONObject(p);
        return "select-by-properties('" + o.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'") + "')";
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    @SuppressWarnings("unused") private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    @SuppressWarnings("unused") private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0, j = 0; i < hex.length(); i += 2, j++) {
            bytes[j] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return bytes;
    }

    @Override public String[] getComponentNames() throws ObjectMapException {
        return omapService.findComponentNames(topContainerAccessor);
    }
}
