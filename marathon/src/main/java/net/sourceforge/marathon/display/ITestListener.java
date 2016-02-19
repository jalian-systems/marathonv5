package net.sourceforge.marathon.display;

import java.util.EventListener;

import junit.framework.Test;

public interface ITestListener extends EventListener {
    public void openTest(Test test);
}
