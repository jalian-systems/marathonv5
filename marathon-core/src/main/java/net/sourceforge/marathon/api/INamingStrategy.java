package net.sourceforge.marathon.api;

import net.sourceforge.marathon.objectmap.ObjectMapException;
import net.sourceforge.marathon.runtime.api.ComponentId;
import net.sourceforge.marathon.runtime.api.IPropertyAccessor;

import org.json.JSONException;
import org.json.JSONObject;

public interface INamingStrategy {

    void save();

    String getName(JSONObject query, String name) throws JSONException, ObjectMapException;

    String getContainerName(JSONObject container) throws JSONException, ObjectMapException;

    void setTopLevelComponent(IPropertyAccessor driver);

    String[] toCSS(ComponentId componentId, boolean visibility) throws ObjectMapException;

    void setDirty();

    String[] getComponentNames() throws ObjectMapException;

}
