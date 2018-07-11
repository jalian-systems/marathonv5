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
package net.sourceforge.marathon.ruby;

import java.util.logging.Logger;

import org.jruby.Ruby;
import org.jruby.RubyString;
import org.jruby.internal.runtime.GlobalVariable.Scope;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

import net.sourceforge.marathon.runtime.api.AbstractDebugger;
import net.sourceforge.marathon.runtime.api.IDebugger;
import net.sourceforge.marathon.runtime.api.IPlaybackListener;
import net.sourceforge.marathon.runtime.api.SourceLine;

public class RubyDebugger extends AbstractDebugger implements IDebugger {

    public static final Logger LOGGER = Logger.getLogger(RubyDebugger.class.getName());

    private final Ruby interpreter;
    private IPlaybackListener listener;
    private String lastEvent;
    private String lastFile;
    private int lastLine;
    private static final String RUBYJAVASUPPORT = System.getProperty("jruby.home");

    public RubyDebugger(Ruby pinterpreter) {
        this.interpreter = pinterpreter;
        addEventHook();
    }

    @Override
    public String run(String script) {
        try {
            script = asciize(script);
            IRubyObject evalScriptlet = interpreter.evalScriptlet(script);
            if (evalScriptlet instanceof RubyString) {
                return RubyScriptModel.inspect(evalScriptlet.toString());
            } else {
                return evalScriptlet.inspect().toString();
            }
        } catch (Throwable t) {
            LOGGER.warning("Script:\n" + script);
            t.printStackTrace();
        }
        return "";
    }

    private String asciize(String script) {
        StringBuilder sb = new StringBuilder();
        byte[] bytes = script.getBytes();
        for (byte b : bytes) {
            if (b < 0) {
                sb.append("\\").append(Integer.toOctalString(b & 0xff));
            } else {
                sb.append((char) b);
            }
        }
        return sb.toString();
    }

    @Override
    public void setListener(IPlaybackListener listener) {
        this.listener = listener;
    }

    public void event(String event, String file, Number line, String name) {
        if (listener != null && !shouldIgnore(file) && !repeat(event, file, line.intValue())) {
            if (event.equals("line")) {
                if (listener.lineReached(new SourceLine(file, name, line.intValue())) == IPlaybackListener.PAUSE) {
                    pause();
                }
            }
            if (event.equals("return")) {
                if (listener.methodReturned(new SourceLine(file, name, line.intValue())) == IPlaybackListener.PAUSE) {
                    pause();
                }
            }
            if (event.equals("call")) {
                if (listener.methodCalled(new SourceLine(file, name, line.intValue())) == IPlaybackListener.PAUSE) {
                    pause();
                }
            }
        }
    }

    private boolean shouldIgnore(String file) {
        return file.equals("<string>") || file.equals("<script>") || file.contains(RUBYJAVASUPPORT)
                || file.contains("uri:classloader");
    }

    private boolean repeat(String event, String file, Integer line) {
        if (event.equals(lastEvent) && file.equals(lastFile) && line.intValue() == lastLine) {
            return true;
        }
        lastEvent = event;
        lastFile = file;
        lastLine = line.intValue();
        return false;
    }

    public void acceptChecklist(String filename) {
        if (listener.acceptChecklist(filename) == IPlaybackListener.PAUSE) {
            pause();
        }
    }

    public void showChecklist(String filename) {
        if (listener.showChecklist(filename) == IPlaybackListener.PAUSE) {
            pause();
        }
    }

    private void addEventHook() {
        String script = "set_trace_func proc { |event, file, line, id, binding, classname| "
                + "$marathon_trace_func.event(event, file, java.lang.Integer.new(line), classname.to_s) }";
        interpreter.defineReadonlyVariable("$marathon_trace_func", JavaEmbedUtils.javaToRuby(interpreter, this), Scope.GLOBAL);
        interpreter.evalScriptlet(script);
    }
}
