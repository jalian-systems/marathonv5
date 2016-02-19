package net.sourceforge.marathon.display;

public interface IScriptConsoleListener {

    public String evaluateScript(String line);

    public void sessionClosed();

}
