package net.sourceforge.marathon.runtime.api;

/**
 * Represents a place to put output of the test script, and its runtime
 * environment
 */
public interface IConsole {
    /**
     * write output of the actual test script
     */
    void writeScriptOut(char cbuf[], int off, int len);

    /**
     * write error stream of the actual test script
     */
    void writeScriptErr(char cbuf[], int off, int len);

    /**
     * write output from the application under test that was written to stdout,
     * or the equivalent
     */
    void writeStdOut(char cbuf[], int off, int len);

    /**
     * write output from the application under test that was written to stderr,
     * or the equivalent.
     */
    void writeStdErr(char cbuf[], int off, int len);

    /**
     * Clear output from console (if possible)
     */
    void clear();
}
