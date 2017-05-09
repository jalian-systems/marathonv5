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
package net.sourceforge.marathon.javaagent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class JTimeouts {

    public static final Logger LOGGER = Logger.getLogger(JTimeouts.class.getName());

    private IJavaAgent agent;

    public JTimeouts(IJavaAgent agent) {
        this.agent = agent;
    }

    public JTimeouts implicitlyWait(long time, TimeUnit unit) {
        agent.setImplicitWait(TimeUnit.MILLISECONDS.convert(Math.max(0, time), unit));
        return this;
    }

}
