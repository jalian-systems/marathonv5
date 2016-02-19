package net.sourceforge.marathon.display;

import java.util.Stack;

import net.sourceforge.marathon.runtime.api.SourceLine;

public class CallStack {
    private static class StackMethod {
        private String methodName;

        public StackMethod(String methodName) {
            this.methodName = methodName;
        }

        @Override public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
            return result;
        }

        @Override public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            StackMethod other = (StackMethod) obj;
            if (methodName == null) {
                if (other.methodName != null)
                    return false;
            } else if (!methodName.equals(other.methodName))
                return false;
            return true;
        }

        @Override public String toString() {
            return methodName;
        }
    }

    private Stack<CallStack.StackMethod> stack = new Stack<CallStack.StackMethod>();
    private int stackDepth = 0;

    public void update(int type, SourceLine line) {
        CallStack.StackMethod elem = new StackMethod(line.functionName);
        if (type == Display.METHOD_RETURNED) {
            if (!stack.contains(elem)) {
                return;
            }
            stackDepth--;
        } else if (type == Display.METHOD_CALLED) {
            stack.push(elem);
            stackDepth++;
        }
    }

    public void clear() {
        stack.clear();
        stackDepth = 0;
    }

    public int getStackDepth() {
        return stackDepth;
    }

}
