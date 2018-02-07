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
package net.sourceforge.marathon.javaagent;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

public abstract class EventQueueWait extends Wait {

    public static final Logger LOGGER = Logger.getLogger(EventQueueWait.class.getName());

    private static final long FOCUS_WAIT_INTERVAL = 50;
    private static final long FOCUS_WAIT = 1000;
    private static Component focusComponent;
    private boolean setupDone = false;

    @Override public boolean until() {
        final boolean[] condition = { false };
        invokeAndWait(new Runnable() {
            @Override public void run() {
                if (!setupDone) {
                    setupDone = true;
                    setup();
                }
                condition[0] = till();
            }
        });
        return condition[0];
    }

    public void wait_noexc(String message, long timeoutInMilliseconds, long intervalInMilliseconds) {
        try {
            super.wait(message, timeoutInMilliseconds, intervalInMilliseconds);
        } catch (Throwable t) {
        }
    }

    /**
     * Returns true when it is time to stop waiting. This method is executed in
     * the Event Dispatch Thread
     *
     * @return
     */
    public abstract boolean till();

    public void setup() {
    }

    @SuppressWarnings("unchecked") public static <X> X exec(final Callable<X> callable) {
        final Object[] result = new Object[] { null };
        final Exception[] exc = new Exception[] { null };
        Runnable r = new Runnable() {
            @Override public void run() {
                try {
                    result[0] = callable.call();
                } catch (Exception e) {
                    exc[0] = e;
                }
            }
        };
        invokeAndWait(r);
        if (exc[0] != null) {
            if (exc[0] instanceof InvocationTargetException) {
                Throwable cause = exc[0].getCause();
                if (cause instanceof Exception) {
                    exc[0] = (Exception) cause;
                } else {
                    exc[0] = new RuntimeException(cause);
                }
            }
            if (exc[0] instanceof RuntimeException) {
                throw (RuntimeException) exc[0];
            }
            throw new JavaAgentException("Call to invokeAndWait failed: " + exc[0].getMessage(), exc[0]);
        }
        return (X) result[0];
    }

    public static void exec(Runnable runnable) {
        try {
            invokeAndWait(runnable);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new JavaAgentException("Call to invokeAndWait failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked") public static <T> T call(final Object o, String f, final Object... args)
            throws NoSuchMethodException {
        Class<?>[] params = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Integer) {
                params[i] = Integer.TYPE;
            } else {
                params[i] = args[i].getClass();
            }
        }
        final Method method;
        try {
            method = o.getClass().getMethod(f, params);
        } catch (SecurityException e1) {
            throw new JavaAgentException("Method " + f + " could not be found: " + e1.getMessage(), e1);
        }
        final Object[] r = new Object[] { null };
        invokeAndWait(new Runnable() {
            @Override public void run() {
                boolean accessible = method.isAccessible();
                try {
                    method.setAccessible(true);
                    r[0] = method.invoke(o, args);
                } catch (Exception e) {
                    r[0] = e;
                } finally {
                    method.setAccessible(accessible);
                }
            }
        });
        if (r[0] instanceof InvocationTargetException) {
            r[0] = ((InvocationTargetException) r[0]).getCause();
        }
        if (r[0] instanceof RuntimeException) {
            throw (RuntimeException) r[0];
        } else if (r[0] instanceof Exception) {
            throw new RuntimeException(((Exception) r[0]).getMessage(), (Exception) r[0]);
        }
        return (T) r[0];
    }

    /**
     * Wait till the event queue is empty.
     */
    public static void empty() {
        new EventQueueWait() {
            @Override public boolean till() {
                return true;
            }
        }.wait("Waiting for the EventQueue to be empty");
    }

    /**
     * Requests for the focus of the component and waits till the component
     * receives focus.
     *
     * @param c
     */
    public static void requestFocus(final Component c) {
        if (EventQueue.isDispatchThread()) {
            if (!c.requestFocusInWindow()) {
                generateFocusEvents(c);
            }
            return;
        }
        try {
            new EventQueueWait() {
                @Override public void setup() {
                    c.requestFocusInWindow();
                    if (!c.isFocusOwner()) {
                        c.requestFocusInWindow();
                    }
                };

                @Override public boolean till() {
                    if (!c.requestFocusInWindow()) {
                        generateFocusEvents(c);
                        return true;
                    }
                    Window w = SwingUtilities.windowForComponent(c);
                    if (w != null) {
                        c.requestFocusInWindow();
                        Component f = w.getFocusOwner();
                        return focused(c, f) || !c.isFocusable();
                    }
                    return false;
                }

                private boolean focused(final Component c, Component f) {
                    if (f == c) {
                        return true;
                    } else if (c instanceof Container) {
                        Component[] cs = ((Container) c).getComponents();
                        for (Component component : cs) {
                            if (focused(component, f)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }.wait("Waiting for the component to receive focus", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
        } catch (Throwable t) {
            // Ignore failure. Most times the actions should work even when the
            // focus is not set.
        }
        focusComponent = c;
    }

    private static void generateFocusEvents(Component c) {
        if (c == focusComponent || !c.isFocusable()) {
            return;
        }
        if (focusComponent != null) {
            dispatchEvent(new FocusEvent(focusComponent, FocusEvent.FOCUS_LOST, false, c));
        }
        dispatchEvent(new FocusEvent(c, FocusEvent.FOCUS_GAINED, false, focusComponent));
    }

    private static void dispatchEvent(final AWTEvent event) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                ((Component) event.getSource()).dispatchEvent(event);
            }
        });
    }

    public static void waitTillDisabled(final Component c) {
        new EventQueueWait() {
            @Override public boolean till() {
                return !c.isEnabled();
            }
        }.wait("Waiting for the component to be disabled", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
    }

    public static void waitTillInvisibled(final Component c) {
        new EventQueueWait() {
            @Override public boolean till() {
                return !c.isVisible();
            }
        }.wait("Waiting for the component to be hidden", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
    }

    public static void waitTillShown(final Component c) {
        new EventQueueWait() {
            @Override public boolean till() {
                return c.isShowing();
            }
        }.wait("Waiting for the component to be shown", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
    }

    private static void invokeAndWait(Runnable r) {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InterruptedException e) {
                throw new RuntimeException("invokeAndWait failed: " + e.getMessage(), e);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }
                throw new RuntimeException("invokeAndWait failed: " + e.getMessage(), e);
            }
        }
    }

    public static <T> T call_noexc(final Object o, String f, final Object... args) {
        try {
            return call(o, f, args);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

}
