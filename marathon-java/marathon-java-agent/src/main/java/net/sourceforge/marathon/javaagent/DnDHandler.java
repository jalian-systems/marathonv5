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
package net.sourceforge.marathon.javaagent;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.dnd.peer.DropTargetContextPeer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

public class DnDHandler {

    private Component source;
    private Component dest;
    private Point location;
    private int dropAction;
    private Transferable transferable;

    public DnDHandler(Component source, Component dest, int x, int y, int dropAction) {
        this.source = source;
        this.dest = dest;
        this.dropAction = dropAction;
        this.location = new Point(x, y);
    }

    public Boolean performDrop() {
        try {
            return EventQueueWait.exec(new Callable<Boolean>() {
                @Override public Boolean call() throws Exception {
                    return performInEQ();
                }
            });
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean performInEQ() {
        if (!(source instanceof JComponent) || !(dest instanceof JComponent))
            return false;
        try {
            Method m;
            TransferHandler th = ((JComponent) source).getTransferHandler();
            if (th == null)
                return false;
            m = getDeclaredMethod(th, "createTransferable", JComponent.class);
            transferable = (Transferable) m.invoke(th, source);
            if (transferable == null)
                return false;
            m = getDeclaredMethod(dest, "dropLocationForPoint", Point.class);
            Object dropLocation = m.invoke(dest, location);
            m = getDeclaredMethod(dest, "setDropLocation", TransferHandler.DropLocation.class, Object.class, Boolean.TYPE);
            m.invoke(dest, dropLocation, null, true);
            dest.getDropTarget().dragEnter(createDropTargetDragEvent());
            dest.getDropTarget().drop(createDropTargetDropEvent());
            m = getDeclaredMethod(th, "exportDone", JComponent.class, Transferable.class, Integer.TYPE);
            m.invoke(th, source, transferable, dropAction);
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    private DropTargetDropEvent createDropTargetDropEvent() throws SecurityException, IllegalArgumentException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return new DropTargetDropEvent(createDropTargetContext(), location, dropAction,
                ((JComponent) source).getTransferHandler().getSourceActions((JComponent) source), true);
    }

    private DropTargetDragEvent createDropTargetDragEvent() throws SecurityException, IllegalArgumentException,
            NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        return new DropTargetDragEvent(createDropTargetContext(), location, dropAction,
                ((JComponent) source).getTransferHandler().getSourceActions((JComponent) source));
    }

    private DropTargetContext createDropTargetContext() throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<DropTargetContext> c = DropTargetContext.class.getDeclaredConstructor(DropTarget.class);
        c.setAccessible(true);
        DropTargetContext inst = c.newInstance(dest.getDropTarget());
        inst.addNotify(createDropTargetContextPeer());
        return inst;
    }

    private DropTargetContextPeer createDropTargetContextPeer() {
        return new DropTargetContextPeer() {
            @Override public void setTargetActions(int actions) {
            }

            @Override public void rejectDrop() {
            }

            @Override public void rejectDrag() {
            }

            @Override public boolean isTransferableJVMLocal() {
                return true;
            }

            @Override public Transferable getTransferable() {
                return transferable;
            }

            @Override public DataFlavor[] getTransferDataFlavors() {
                return transferable.getTransferDataFlavors();
            }

            @Override public int getTargetActions() {
                return dropAction;
            }

            @Override public DropTarget getDropTarget() {
                return dest.getDropTarget();
            }

            @Override public void dropComplete(boolean success) {
            }

            @Override public void acceptDrop(int dropAction) {
            }

            @Override public void acceptDrag(int dragAction) {
            }
        };
    }

    private Method getDeclaredMethod(Object th, String n, Class<?>... classes) {
        Method method = null;
        Class<?> klass = th.getClass();
        while (klass != Object.class) {
            try {
                method = klass.getDeclaredMethod(n, classes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                klass = klass.getSuperclass();
            }
        }
        return method;
    }
}
