package net.sourceforge.marathon.fx.display;

import java.util.logging.Logger;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import net.sourceforge.marathon.runtime.api.Constants;

public final class TextAreaLimited extends TextArea {

    public static final Logger LOGGER = Logger.getLogger(TextAreaLimited.class.getName());

    private static final String DEFAULT_STYLE = "-fx-font-family: Monaco, \"Lucida Console\", monospace;-fx-font-size: 14px;-fx-text-fill:blue;";
    private static final int MAX_CHARS = Integer.getInteger(Constants.OUTPUT_MAX_CHARS, 100 * 1024);
    private static final int BSIZ = MAX_CHARS / 10;
    private boolean internalMod;

    public TextAreaLimited() {
        setStyle(DEFAULT_STYLE + System.getProperty(Constants.OUTPUT_STYLE, ""));
        setTextFormatter(new TextFormatter<String>(change -> !change.isContentChange() || internalMod ? change : null));
    }

    @Override public void appendText(String text) {
        try {
            internalMod = true;
            if (getLength() > MAX_CHARS) {
                replaceText(0, BSIZ, "");
            }
            super.appendText(text);
        } finally {
            internalMod = false;
        }
    }

    @Override public void clear() {
        try {
            internalMod = true;
            super.clear();
        } finally {
            internalMod = false;
        }
    }
}