package net.sourceforge.marathon.editor;

public interface IStatusBar {

    public abstract void setCaretLocation(int row, int col);

    public abstract void setIsOverwriteEnabled(boolean isOverwriteEnabled);

}
