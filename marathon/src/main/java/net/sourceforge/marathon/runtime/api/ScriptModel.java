package net.sourceforge.marathon.runtime.api;

public abstract class ScriptModel {

    private static IScriptModel instance;

    public static void initialize() {
        String property = System.getProperty(Constants.PROP_PROJECT_SCRIPT_MODEL);
        if (property == null)
            throw new IllegalArgumentException("Script model not set");
        try {
            instance = getModel(property);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Script model " + property + " not found - check class path");
        }
    }

    private static IScriptModel getModel(String selectedScript) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        Class<?> klass = Class.forName(selectedScript);
        return (IScriptModel) klass.newInstance();
    }

    public static IScriptModel getModel() {
        if (instance == null)
            initialize();
        return instance;
    }
}
