package com.jaliansystems.marathonite.api;

import java.util.List;

public interface IAdditionalActionProvider {

    List<?> getActions(Object editorProvider);

}
