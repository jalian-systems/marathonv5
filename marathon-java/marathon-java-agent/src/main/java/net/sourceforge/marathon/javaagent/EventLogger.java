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

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.logging.Logger;

public class EventLogger {
    public static final Logger LOGGER = Logger.getLogger(EventLogger.class.getName());

    public EventLogger(String eventsToLog) {
        long events = parse(eventsToLog);
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                log(event);
            }
        }, events);
    }

    private long parse(String eventsToLog) {
        if (eventsToLog == null || "".equals(eventsToLog)) {
            return 0;
        }
        String[] eventArray = eventsToLog.split(",");
        long events = 0;
        for (String event : eventArray) {
            String awtEventName = event.toUpperCase().trim() + "_MASK";
            try {
                long events_v = ((Long) AWTEvent.class.getField(awtEventName).get(null)).longValue();
                events |= events_v;
                LOGGER.info("Enabled event logging for " + awtEventName);
            } catch (Throwable t) {
                LOGGER.warning("Event mask not found for " + awtEventName);
            }
        }
        return events;
    }

    public void log(final AWTEvent e) {
        LOGGER.info(e.toString());
    }
}
