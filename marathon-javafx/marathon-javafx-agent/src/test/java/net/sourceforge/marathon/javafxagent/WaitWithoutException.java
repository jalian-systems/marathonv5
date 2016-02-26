package net.sourceforge.marathon.javafxagent;

public abstract class WaitWithoutException extends Wait {

    @Override public void wait(String message, long timeoutInMilliseconds, long intervalInMilliseconds) {
        try {
            super.wait(message, timeoutInMilliseconds, intervalInMilliseconds);
        } catch (Throwable t) {
        }
    }
}
