package net.sourceforge.marathon.objectmap;

import java.util.List;

public interface IOMapContainer {

    public abstract OMapContainer getOMapContainer(ObjectMap objectMap);

    public abstract List<String> getUsedRecognitionProperties();

}
