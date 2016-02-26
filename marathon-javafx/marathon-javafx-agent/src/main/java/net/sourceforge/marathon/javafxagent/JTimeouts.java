package net.sourceforge.marathon.javafxagent;

import java.util.concurrent.TimeUnit;

public class JTimeouts {

    private IJavaAgent agent;

    public JTimeouts(IJavaAgent agent) {
        this.agent = agent;
    }
    
    public JTimeouts implicitlyWait(long time, TimeUnit unit) {
        agent.setImplicitWait(TimeUnit.MILLISECONDS.convert(Math.max(0, time), unit));
        return this;
    }

}