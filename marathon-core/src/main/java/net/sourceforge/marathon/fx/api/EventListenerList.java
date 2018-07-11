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
package net.sourceforge.marathon.fx.api;

import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.logging.Logger;

public class EventListenerList {

    public static final Logger LOGGER = Logger.getLogger(EventListenerList.class.getName());

    /* A null array to be shared by all empty listener lists */
    private final static Object[] NULL_ARRAY = new Object[0];
    /* The list of ListenerType - Listener pairs */
    protected transient Object[] listenerList = NULL_ARRAY;

    public synchronized <T extends EventListener> void add(Class<T> t, T l) {
        if (l == null) {
            // In an ideal world, we would do an assertion here
            // to help developers know they are probably doing
            // something wrong
            return;
        }
        if (!t.isInstance(l)) {
            throw new IllegalArgumentException("Listener " + l + " is not of type " + t);
        }
        if (listenerList == NULL_ARRAY) {
            // if this is the first listener added,
            // initialize the lists
            listenerList = new Object[] { t, l };
        } else {
            // Otherwise copy the array and add the new listener
            int i = listenerList.length;
            Object[] tmp = new Object[i + 2];
            System.arraycopy(listenerList, 0, tmp, 0, i);

            tmp[i] = t;
            tmp[i + 1] = l;

            listenerList = tmp;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends EventListener> T[] getListeners(Class<T> t) {
        Object[] lList = listenerList;
        int n = getListenerCount(lList, t);
        T[] result = (T[]) Array.newInstance(t, n);
        int j = 0;
        for (int i = lList.length - 2; i >= 0; i -= 2) {
            if (lList[i] == t) {
                result[j++] = (T) lList[i + 1];
            }
        }
        return result;
    }

    public int getListenerCount(Class<?> t) {
        Object[] lList = listenerList;
        return getListenerCount(lList, t);
    }

    private int getListenerCount(Object[] list, Class<?> t) {
        int count = 0;
        for (int i = 0; i < list.length; i += 2) {
            if (t == (Class<?>) list[i]) {
                count++;
            }
        }
        return count;
    }

}
