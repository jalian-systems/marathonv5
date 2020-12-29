package net.sourceforge.marathon.json;

import java.io.InputStream;

public class JSONTokener {

    private InputStream resourceAsStream;

    public JSONTokener(InputStream resourceAsStream) {
        this.setResourceAsStream(resourceAsStream);
    }

    public InputStream getResourceAsStream() {
        return resourceAsStream;
    }

    public void setResourceAsStream(InputStream resourceAsStream) {
        this.resourceAsStream = resourceAsStream;
    }

}
