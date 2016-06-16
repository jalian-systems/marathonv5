package net.sourceforge.marathon.editor.rsta;

public class Selection {

    private final String text;
    private final int startOffset;
    private final int endOffset;
    private final int startLine;
    private final int endLine;

    public Selection(String text, int startOffset, int endOffset, int startLine, int endLine) {
        this.text = text;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.startLine = startLine;
        this.endLine = endLine;
    }

    public String getText() {
        return text;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

}
