package net.sourceforge.marathon.javafxagent.css;

import java.util.List;

import net.sourceforge.marathon.javafxagent.IJavaFXAgent;
import net.sourceforge.marathon.javafxagent.IJavaFXElement;

public class FindByCssSelector {

    private IJavaFXElement container;
    private long implicitWait;
    private IJavaFXAgent driver;

    public FindByCssSelector(IJavaFXElement container, IJavaFXAgent driver2, long implicitWait) {
        this.container = container;
        this.driver = driver2;
        this.implicitWait = implicitWait;
    }

    public List<IJavaFXElement> findElements(String using) {
        Selector selector = new SelectorParser(using).parse();
        return selector.findElements(driver, container, implicitWait);
    }

}
