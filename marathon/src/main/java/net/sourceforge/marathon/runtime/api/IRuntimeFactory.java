package net.sourceforge.marathon.runtime.api;

/**
 * Responsible for instantiating runtimes based on the the given
 * <code>RuntimeProfile</code>. The output of this Runtime will be directed to
 * <code>Console</code>
 */
public interface IRuntimeFactory {
    /**
     * create and return a new runtime object
     * 
     * @param logViewLogger
     */
    IMarathonRuntime createRuntime();
}
