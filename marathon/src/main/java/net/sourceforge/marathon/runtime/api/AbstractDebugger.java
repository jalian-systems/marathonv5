package net.sourceforge.marathon.runtime.api;

public abstract class AbstractDebugger implements IDebugger {
    private String commandToExecute;
    private String returnValue = null;
    private Object commandLock = new Object();

    public void pause() {
        while (true) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (commandToExecute == null)
                    break;
                try {
                    returnValue = run(commandToExecute);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                synchronized (commandLock) {
                    commandLock.notifyAll();
                }
                commandToExecute = null;
            }
        }
    }

    public void resume() {
        synchronized (this) {
            this.notifyAll();
        }
    }

    public String evaluateScriptWhenPaused(String script) {
        commandToExecute = script;
        returnValue = "";
        synchronized (commandLock) {
            resume();
            try {
                commandLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return returnValue;
    }
}
