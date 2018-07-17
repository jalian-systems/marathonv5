package net.sourceforge.marathon.util;

import java.awt.Desktop;
import java.net.URI;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

public class HyperlinkRedirectListener implements ChangeListener<State>, EventListener {
    private static final Logger LOGGER = Logger.getLogger(HyperlinkRedirectListener.class.getName());

    private static final String CLICK_EVENT = "click";
    private static final String ANCHOR_TAG = "a";

    private final WebView webView;

    public HyperlinkRedirectListener(WebView webView) {
        this.webView = webView;
    }

    @Override
    public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
        if (State.SUCCEEDED.equals(newValue)) {
            Document document = webView.getEngine().getDocument();
            NodeList anchors = document.getElementsByTagName(ANCHOR_TAG);
            for (int i = 0; i < anchors.getLength(); i++) {
                Node node = anchors.item(i);
                EventTarget eventTarget = (EventTarget) node;
                eventTarget.addEventListener(CLICK_EVENT, this, false);
            }
        }
    }

    @Override
    public void handleEvent(Event event) {
        HTMLAnchorElement anchorElement = (HTMLAnchorElement) event.getCurrentTarget();
        String href = anchorElement.getHref();

        if (Desktop.isDesktopSupported()) {
            openLinkInSystemBrowser(href);
        } else {
            LOGGER.warning("OS does not support desktop operations like browsing. Cannot open link '{" + href + "}'.");
        }

        event.preventDefault();
    }

    private void openLinkInSystemBrowser(String url) {
        LOGGER.info("Opening link '{" + url + "}' in default system browser.");

        try {
            URI uri = new URI(url);
            Desktop.getDesktop().browse(uri);
        } catch (Throwable e) {
            LOGGER.warning("Error on opening link '{" + url + "}' in system browser.");
        }
    }
}