package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class PseudoClassFilter implements SelectorFilter {

    private String function;
    private Argument[] args;

    public PseudoClassFilter(String function) {
        this(function, new Argument[0]);
    }

    public PseudoClassFilter(String function, Argument[] args) {
        this.function = function;
        this.args = args;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder(":" + function);
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
        if (doesMatch(je))
            return Arrays.asList(je);
        return new ArrayList<IJavaFXElement>();
    }

    public boolean doesMatch(IJavaFXElement je) {
        if (args == null || args.length == 0)
            return je.filterByPseudoClass(function);
        Object[] params = new Object[args.length];
        for (int i = 0; i < args.length; i++)
            params[i] = args[i].getValue();
        return je.filterByPseudoClass(function, params);
    }

    public boolean isNth() {
        return function.equals("nth");
    }

    public int getNthIndex() {
        return (Integer) args[0].getValue();
    }
}
