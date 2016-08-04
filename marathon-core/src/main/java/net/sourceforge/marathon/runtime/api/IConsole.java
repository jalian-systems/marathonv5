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
