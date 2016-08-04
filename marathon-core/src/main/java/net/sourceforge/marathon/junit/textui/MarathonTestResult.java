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
package net.sourceforge.marathon.junit.textui;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.runner.TestRunListener;

public class MarathonTestResult {
    public static final int STATUS_NONE = -1;
    public static final int STATUS_PASS = 0;
    public static final int STATUS_ERROR = TestRunListener.STATUS_ERROR;
    public static final int STATUS_FAILURE = TestRunListener.STATUS_FAILURE;
    private Test test;
    private Throwable throwable;
    private double duration;
    private int status;

    public MarathonTestResult(Test test) {
        this.test = test;
        status = STATUS_PASS;
    }

    public final double getDuration() {
        return duration;
    }

    public final int getStatus() {
        return status;
    }

    public final Test getTest() {
        return test;
    }

    public final String getTestName() {
        return ((TestCase) test).getName();
    }

    public final Throwable getThrowable() {
        return throwable;
    }

    public final void setDuration(double duration) {
        this.duration = duration;
    }

    public final void setStatus(int status) {
        this.status = status;
    }

    public final void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public final String getStatusDescription() {
        String desc = status == STATUS_PASS ? "Passed" : status == STATUS_ERROR ? "Caused an ERROR" : "FAILED";
        return desc;
    }
}
