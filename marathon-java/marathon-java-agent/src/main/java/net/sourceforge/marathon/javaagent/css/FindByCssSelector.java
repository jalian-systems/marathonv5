package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaAgent;
import net.sourceforge.marathon.javaagent.IJavaElement;

public class FindByCssSelector {

    private IJavaElement container;
    private long implicitWait;
    private IJavaAgent driver;

    public FindByCssSelector(IJavaElement container, IJavaAgent driver2, long implicitWait) {
        this.container = container;
        this.driver = driver2;
        this.implicitWait = implicitWait;
    }

    public List<IJavaElement> findElements(String using) {
        Selector selector = new SelectorParser(using).parse();
        return selector.findElements(driver, container, implicitWait);
    }

}
