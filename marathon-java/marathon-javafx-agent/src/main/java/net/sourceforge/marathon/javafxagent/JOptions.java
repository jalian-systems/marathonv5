package net.sourceforge.marathon.javafxagent;

public class JOptions {

    private JTimeouts timeouts ;
    private IJavaAgent agent;

    public JTimeouts timeouts() {
        if(timeouts == null)
            timeouts = new JTimeouts(agent);
        return timeouts;
    }

    public JOptions(IJavaAgent agent) {
        this.agent = agent;
    }
}