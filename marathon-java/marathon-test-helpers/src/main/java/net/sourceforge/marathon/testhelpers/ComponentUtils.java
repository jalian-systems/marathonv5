package net.sourceforge.marathon.testhelpers;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;

public class ComponentUtils {

    public static Component findComponent(Class<?> ofClass, Container inContainer) {
        Component[] cs = inContainer.getComponents();
        for (Component component : cs) {
            if(ofClass.isAssignableFrom(component.getClass()))
                return component ;
            if(component instanceof Container) {
                Component child = findComponent(ofClass, (Container) component);
                if(child != null)
                    return child ;
            }
        }
        return null;
    }

    public static List<Component> findComponents(Class<?> ofClass, Container inContainer) {
        List<Component> l = new ArrayList<Component>();
        return findComponents(ofClass, inContainer, l);
    }

    private static List<Component> findComponents(Class<?> ofClass, Container inContainer, List<Component> l) {
        Component[] cs = inContainer.getComponents();
        for (Component component : cs) {
            if(ofClass.isAssignableFrom(component.getClass()))
                l.add(component) ;
            if(component instanceof Container) {
                findComponents(ofClass, (Container) component, l);
            }
        }
        return l;
    }

}
