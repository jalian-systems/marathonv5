package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaAgent;
import net.sourceforge.marathon.javafxagent.IJavaElement;

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
