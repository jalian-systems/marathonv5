package net.sourceforge.marathon.javafxagent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.scene.Node;

public abstract class EventQueueWait extends Wait {

    private static final long FOCUS_WAIT_INTERVAL = 50;
    private static final long FOCUS_WAIT = 1000;
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
                if (cause instanceof Exception)
                    exc[0] = (Exception) cause;
                else
                    exc[0] = new RuntimeException(cause);
            }
            if (exc[0] instanceof RuntimeException)
                throw ((RuntimeException) exc[0]);
            throw new JavaAgentException("Call to invokeAndWait failed: " + exc[0].getMessage(), exc[0]);
        }
        return (X) result[0];
    }

    public static void exec(Runnable runnable) {
        try {
            invokeAndWait(runnable);
        } catch (RuntimeException e) {
            throw ((RuntimeException) e);
        } catch (Exception e) {
            throw new JavaAgentException("Call to invokeAndWait failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked") public static <T> T call(final Object o, String f, final Object... args)
            throws NoSuchMethodException {
        Class<?>[] params = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Integer)
                params[i] = Integer.TYPE;
            else
                params[i] = args[i].getClass();
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
                try {
                    r[0] = method.invoke(o, args);
                } catch (Exception e) {
                    r[0] = e;
                }
            }
        });
        if (r[0] instanceof InvocationTargetException) {
            r[0] = ((InvocationTargetException) r[0]).getCause();
        }
        if (r[0] instanceof RuntimeException)
            throw ((RuntimeException) r[0]);
        else if (r[0] instanceof Exception)
            throw new RuntimeException(((Exception) r[0]).getMessage(), (Exception) r[0]);
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
    public static void requestFocus(final Node c) {
        try {
            new EventQueueWait() {
                public void setup() {
                    c.requestFocus();
                };

                @Override public boolean till() {
                    return c.isFocused();
                }

            }.wait("Waiting for the component to receive focus", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
        } catch (Throwable t) {
            // Ignore failure. Most times the actions should work even when the
            // focus is not set.
        }
    }

    public static void waitTillDisabled(final Node c) {
        new EventQueueWait() {
            @Override public boolean till() {
                return c.isDisabled();
            }
        }.wait("Waiting for the component to be disabled", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
    }

    public static void waitTillInvisibled(final Node c) {
        new EventQueueWait() {
            @Override public boolean till() {
                return !c.isVisible();
            }
        }.wait("Waiting for the component to be hidden", FOCUS_WAIT, FOCUS_WAIT_INTERVAL);
    }

    private static void invokeAndWait(final Runnable r) {
        if (Platform.isFxApplicationThread())
            r.run();
        else {
            final boolean[] lock = new boolean[] { false };
            Runnable r1 = new Runnable() {
                @Override public void run() {
                    r.run();
                    synchronized (lock) {
                        lock[0] = true;
                        lock.notifyAll();
                    }
                }
            };
            Platform.runLater(r1);
            synchronized (lock) {
                while (true) {
                    try {
                        if(lock[0])
                            break;
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
