package net.sourceforge.marathon.display;

public interface IStdOut {
    public static final int STD_OUT = 1;
    public static final int STD_ERR = 2;
    public static final int SCRIPT_OUT = 3;
    public static final int SCRIPT_ERR = 4;

    public abstract String getText();

    public abstract void append(String text, int type);

    public abstract void clear();
}
