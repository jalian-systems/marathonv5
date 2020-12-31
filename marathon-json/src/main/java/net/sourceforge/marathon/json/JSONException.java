package net.sourceforge.marathon.json;

@SuppressWarnings("serial")
public class JSONException extends RuntimeException {

    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONException(Throwable exception) {
        super(exception);
    }

    public JSONException(String string) {
        super(string);
    }

}
