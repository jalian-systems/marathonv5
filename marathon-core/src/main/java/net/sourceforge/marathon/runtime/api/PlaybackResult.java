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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class PlaybackResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Collection<Failure> failures = new LinkedList<Failure>();

    public void addFailure(String message, SourceLine[] traceback, Throwable t) {
        failures.add(new Failure(message, traceback, t));
    }

    public Failure[] failures() {
        return failures.toArray(new Failure[failures.size()]);
    }

    public int failureCount() {
        return failures.size();
    }

    public boolean hasFailure() {
        return failureCount() > 0;
    }

    public void addFailure(Failure[] f) {
        failures.addAll(Arrays.asList(f));
    }
}
