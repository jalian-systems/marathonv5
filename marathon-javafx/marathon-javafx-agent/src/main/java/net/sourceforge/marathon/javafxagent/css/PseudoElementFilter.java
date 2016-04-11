package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class PseudoElementFilter implements SelectorFilter {

    private String function;
    private Argument[] args;

    public PseudoElementFilter(String function) {
        this(function, new Argument[0]);
    }

    public PseudoElementFilter(String function, Argument[] args) {
        this.function = function;
        this.args = args;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("::" + function);
        if (args.length > 0) {
            sb.append("(");
            for (Argument arg : args) {
                sb.append(arg.toString());
                sb.append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(")");
        }
        return sb.toString();
    }

    @Override public List<IJavaFXElement> match(IJavaFXElement je) {
        Object[] params = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            params[i] = args[i].getValue();
        return je.getByPseudoElement(function, params);
    }
}
