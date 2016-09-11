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
package net.sourceforge.marathon.javaagent.script;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import javax.swing.SwingUtilities;

import net.sourceforge.marathon.javaagent.JavaAgentException;
import net.sourceforge.marathon.javaagent.server.ExecuteMode;

public class ScriptExecutor {
    private ExecuteMode mode;
    private Object result;
    private boolean callback = false;

    public static interface Callback {
        void call(Object o);
    }

    public ScriptExecutor(ExecuteMode mode) {
        this.mode = mode;
    }

    public Object executeScript(String methodBody, final Object[] args) throws CannotCompileException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException, InterruptedException {
        ClassPool cp = ClassPool.getDefault();
        cp.importPackage("javax.swing");
        cp.importPackage("java.lang.reflect.Array");
        CtClass helloClass = cp.makeClass(getClassName());
        CtMethod make = CtNewMethod.make(getMethodBody(args), helloClass);
        if(!methodBody.endsWith(";"))
            methodBody = methodBody + ";";
        make.insertBefore(methodBody);
        Logger.getLogger(ScriptExecutor.class.getName()).log(Level.INFO, "Method Body:\n" + methodBody);
        helloClass.addMethod(make);
        final Class<?> helloClazz = helloClass.toClass();
        final Method declaredMethod = helloClazz.getDeclaredMethod("execute", getMethodParams(args));
        if (mode == ExecuteMode.ASYNC) {
            final Object[] newArgs = new Object[args.length + 1];
            System.arraycopy(args, 0, newArgs, 0, args.length);
            newArgs[args.length] = new Callback() {
                @Override public void call(Object o) {
                    result = o;
                    callback = true;
                    synchronized (ScriptExecutor.this) {
                        ScriptExecutor.this.notifyAll();
                    }
                }
            };
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    new Runnable() {
                        @Override public void run() {
                            try {
                                declaredMethod.invoke(helloClazz.newInstance(), newArgs);
                            } catch (Exception e) {
                                result = e;
                            }
                        }
                    }.run();
                }
            });
            synchronized (this) {
                try {
                    wait(5000);
                } catch (InterruptedException e) {
                    result = e;
                }
            }
            if (!callback)
                throw new JavaAgentException("Expected callback not occured. Use $2.call(...) to perform callback", null);
            return result;
        }
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override public void run() {
                try {
                    result = declaredMethod.invoke(helloClazz.newInstance(), args);
                } catch (Exception e) {
                    result = e;
                }
            }
        });
        return result;
    }

    private static final String CLASS_PREFIX = "MarathonExecScript";
    private static int count = 0;

    public static String getClassName() {
        return CLASS_PREFIX + count++;
    }

    public Class<?>[] getMethodParams(Object[] args) {
        Class<?>[] r = new Class[mode == ExecuteMode.SYNC ? args.length : args.length + 1];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null)
                r[i] = Object.class;
            else if (args[i].getClass().isArray())
                r[i] = Object[].class;
            else
                r[i] = args[i].getClass();
        }
        if (mode == ExecuteMode.ASYNC)
            r[args.length] = Callback.class;
        return r;
    }

    public String getMethodBody(Object[] args) {
        String r = "public Object execute(" + getMethodArgs(args) + ")";
        Logger.getLogger(ScriptExecutor.class.getName()).log(Level.INFO, "Function signature: " + r);
        return r + " { return null ;}";
    }

    public String getMethodArgs(Object[] args) {
        int index = 0;
        StringBuilder sb = new StringBuilder();
        for (Object o : args) {
            if (o == null)
                sb.append("Object a" + index++).append(",");
            else if (o.getClass().isArray())
                sb.append("Object[] a" + index++).append(",");
            else
                sb.append(o.getClass().getName() + " a" + index++).append(",");
        }
        if (mode == ExecuteMode.ASYNC) {
            sb.append(Callback.class.getName() + " a" + index++).append(",");
        }
        if (sb.length() > 0)
            sb.setLength(sb.length() - 1);
        return sb.toString();
    }

}
