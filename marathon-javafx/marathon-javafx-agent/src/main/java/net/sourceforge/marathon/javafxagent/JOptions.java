package net.sourceforge.marathon.javafxagent;

public class JOptions {

    private JTimeouts timeouts;
    private IJavaFXAgent agent;

    public JTimeouts timeouts() {
        if (timeouts == null)
            timeouts = new JTimeouts(agent);
        return timeouts;
    }

    public JOptions(IJavaFXAgent agent) {
        this.agent = agent;
    }
}