package net.sourceforge.marathon.javaagent;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.util.logging.Logger;

public class EventLogger {
    private static final Logger logger = Logger.getLogger(EventLogger.class.getName());

    public EventLogger(String eventsToLog) {
        long events = parse(eventsToLog);
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override public void eventDispatched(AWTEvent event) {
                log(event);
            }
        }, events);
    }

    private long parse(String eventsToLog) {
        if (eventsToLog == null || "".equals(eventsToLog))
            return 0;
        String[] eventArray = eventsToLog.split(",");
        long events = 0;
        for (String event : eventArray) {
            String awtEventName = event.toUpperCase().trim() + "_MASK";
            try {
                long events_v = ((Long) AWTEvent.class.getField(awtEventName).get(null)).longValue();
                events |= events_v;
                logger.info("Enabled event logging for " + awtEventName);
            } catch (Throwable t) {
                logger.warning("Event mask not found for " + awtEventName);
            }
        }
        return events;
    }

    public void log(final AWTEvent e) {
        logger.info(e.toString());
    }
}