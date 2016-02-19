package net.sourceforge.marathon.javaagent.css;

public class Argument {

    private Boolean b;
    private Integer i;
    private Double d;
    private String s;

    public Argument(Boolean b) {
        this.b = b;
    }

    public Argument(int i) {
        this.i = Integer.valueOf(i);
    }

    public Argument(double d) {
        this.d = Double.valueOf(d);
    }

    public Argument(String s) {
        this.s = s;
    }

    @Override public String toString() {
        if (b != null)
            return b.toString();
        else if (i != null)
            return i.toString();
        else if (d != null)
            return d.toString();
        else
            return "\"" + s + "\"";
    }

    public String getStringValue() {
        if (b != null)
            return b.toString();
        else if (i != null)
            return i.toString();
        else if (d != null)
            return d.toString();
        else
            return s;
    }

    public Object getValue() {
        if (b != null)
            return b;
        else if (i != null)
            return i;
        else if (d != null)
            return d;
        else
            return s;
    }
}
