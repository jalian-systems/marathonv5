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
package net.sourceforge.marathon.display;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import net.sourceforge.marathon.util.AbstractSimpleAction;

public class ActionInjector {

    public static final Logger LOGGER = Logger.getLogger(ActionInjector.class.getName());

    private final Object o;

    public ActionInjector(Object o) {
        this.o = o;
    }

    public void injectActions() {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                ISimpleAction annotation = field.getAnnotation(ISimpleAction.class);
                if (annotation == null) {
                    continue;
                }
                AbstractSimpleAction action = createAction(field.getName(), annotation);
                field.set(o, action);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                field.setAccessible(accessible);
            }
        }
    }

    private AbstractSimpleAction createAction(String fieldName, ISimpleAction annotation)
            throws SecurityException, NoSuchMethodException {
        String name = findName(fieldName);
        String commandName = annotation.value();
        if (commandName.equals("")) {
            commandName = null;
        }
        String mneumonic = annotation.mneumonic();
        String action = annotation.action();
        if (action.equals("")) {
            action = findAction(name);
        }
        final Method m = findMethod(action);
        String description = annotation.description();
        if (description.equals("")) {
            description = commandName;
        }
        return new AbstractSimpleAction(name, description, mneumonic, commandName) {
            private static final long serialVersionUID = 1L;

            @Override public void handle(ActionEvent e) {
                try {
                    m.invoke(o);
                } catch (IllegalArgumentException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                } catch (InvocationTargetException e1) {
                    e1.printStackTrace();
                }
            }
        };
    }

    private String findName(String fieldName) {
        if (fieldName.endsWith("Action")) {
            fieldName = fieldName.substring(0, fieldName.length() - 6);
        }
        return fieldName;
    }

    private Method findMethod(String action) throws SecurityException, NoSuchMethodException {
        return o.getClass().getMethod(action);
    }

    private String findAction(String fieldName) {
        return "on" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
    }

}
