/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.junit.textui;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;
import net.sourceforge.marathon.junit.MarathonAssertion;
import net.sourceforge.marathon.runtime.api.Failure;

public final class MarathonJunitTestResult extends TestResult {
    @Override public synchronized void addFailure(Test test, AssertionFailedError e) {
        if (e instanceof MarathonAssertion) {
            Failure[] failures = ((MarathonAssertion) e).getFailures();
            for (Failure failure : failures) {
                if (failure.getThrowable() == null || !(failure.getThrowable() instanceof AssertionFailedError)) {
                    super.addError(test, e);
                    return;
                }
            }
        }
        super.addFailure(test, e);
    }

    @Override public String toString() {
        return "[runCount = " + runCount() + " errors = " + errorCount() + " failures = " + failureCount() + "]";
    }
}
