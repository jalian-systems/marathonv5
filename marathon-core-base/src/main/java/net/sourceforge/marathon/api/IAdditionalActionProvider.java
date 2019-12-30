package net.sourceforge.marathon.api;

import java.util.List;

public interface IAdditionalActionProvider {

    List<?> getActions(Object editorProvider);

}
