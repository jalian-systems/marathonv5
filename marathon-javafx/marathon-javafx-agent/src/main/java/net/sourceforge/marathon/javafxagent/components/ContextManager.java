/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.javafxagent.components;

import java.util.LinkedList;

import javafx.scene.Node;

public class ContextManager {

    private static class InstanceCheck implements IContextChecker {

        private Class<? extends Node> componentClass;

        public InstanceCheck(Class<? extends Node> klass) {
            componentClass = klass;
        }

        @Override public boolean isContext(Node c) {
            return componentClass.isInstance(c);
        }

    }

    private static LinkedList<IContextChecker> containers = new LinkedList<IContextChecker>();

    static {
    }

    public static void add(Class<? extends Node> klass) {
        add(new InstanceCheck(klass));
    }

    public static void add(IContextChecker e) {
        containers.addFirst(e);
    }

    public static boolean isContext(Node c) {
        for (IContextChecker checker : containers) {
            if (checker.isContext(c))
                return true;
        }
        return false;
    }

}
