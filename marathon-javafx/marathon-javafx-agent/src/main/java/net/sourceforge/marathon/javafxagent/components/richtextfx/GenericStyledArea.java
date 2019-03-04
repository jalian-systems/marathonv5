package net.sourceforge.marathon.javafxagent.components.richtextfx;

import java.lang.reflect.InvocationTargetException;

import javafx.collections.ObservableMap;
import javafx.scene.Node;

public class GenericStyledArea {

    public static final String GENERIC_STYLED_AREA_CLASS = "org.fxmisc.richtext.GenericStyledArea";

    public static Class<?> genericStyledAreaKlass;
    public static Class<?> codeAreaKlass;
    public static Class<?> styleCTextAreaKlass;
    public static Class<?> inlineCSSKlass;

    static {
        try {
            genericStyledAreaKlass = Class.forName(GENERIC_STYLED_AREA_CLASS);
            codeAreaKlass = Class.forName("org.fxmisc.richtext.CodeArea");
            inlineCSSKlass = Class.forName("org.fxmisc.richtext.InlineCssTextArea");
            styleCTextAreaKlass = Class.forName("org.fxmisc.richtext.StyleClassedTextArea");
        } catch (ClassNotFoundException e) {
        }
    }

    private Object genericStyledArea;

    public GenericStyledArea(Object component) {
        this.genericStyledArea = component;
    }

    @SuppressWarnings("unchecked")
    public ObservableMap<Object, Object> getProperties() {
        try {
            return (ObservableMap<Object, Object>) genericStyledAreaKlass.getMethod("getProperties").invoke(genericStyledArea);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        }
        return null;
    }

    public void clear() {
        try {
            genericStyledAreaKlass.getMethod("clear").invoke(genericStyledArea);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        }
    }

    public String getText() {
        try {
            return (String) genericStyledAreaKlass.getMethod("getText").invoke(genericStyledArea);
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        }
        return null;
    }

    public static String getTagName(Node component) {
        if (codeAreaKlass.isInstance(component)) {
            return "code-area";
        } else if (inlineCSSKlass.isInstance(component)) {
            return "inline-css-text-area";
        } else if (styleCTextAreaKlass.isInstance(component)) {
            return "style-classed-text-area";
        }
        return "generic-styled-area";
    }

}
