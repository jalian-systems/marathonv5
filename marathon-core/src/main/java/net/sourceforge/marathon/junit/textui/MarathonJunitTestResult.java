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
