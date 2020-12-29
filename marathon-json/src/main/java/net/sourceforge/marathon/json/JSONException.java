package net.sourceforge.marathon.json;

import java.io.IOException;

@SuppressWarnings("serial")
public class JSONException extends RuntimeException {

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(IOException exception) {
        super(exception);
    }

    public JSONException(String string) {
        super(string);
    }

}
