package net.sourceforge.marathon.javaagent.css;

import java.util.List;

import net.sourceforge.marathon.javaagent.IJavaElement;
import net.sourceforge.marathon.javaagent.JavaAgent;

public class FindByCssSelector {

    private IJavaElement container;
    private long implicitWait;
    private JavaAgent driver;

    public FindByCssSelector(IJavaElement container, JavaAgent driver, long implicitWait) {
        this.container = container;
        this.driver = driver;
        this.implicitWait = implicitWait;
    }

    public List<IJavaElement> findElements(String using) {
        Selector selector = new SelectorParser(using).parse();
        return selector.findElements(driver, container, implicitWait);
    }

}
