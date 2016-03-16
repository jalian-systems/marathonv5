package net.sourceforge.marathon.runtime.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.JSONObject;

public class JSONObjectPropertyAccessor extends DefaultMatcher implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient JSONObject o;

    public JSONObjectPropertyAccessor() {
    }

    public JSONObjectPropertyAccessor(JSONObject o) {
        this.o = o;
    }

    @Override public String getProperty(String name) {
        if (o.has(name))
            return o.getString(name);
        return null;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(o.toString());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        o = new JSONObject((String) s.readObject());
    }
}
