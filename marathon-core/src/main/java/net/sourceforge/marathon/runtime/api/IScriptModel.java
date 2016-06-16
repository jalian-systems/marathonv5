package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;

public interface IScriptModel extends ISubpanelProvider {

    public enum SCRIPT_FILE_TYPE {
        TEST, MODULE, FIXTURE, OTHER
    }

    public abstract String getDefaultTestHeader(String fixture);

    public abstract String getFixtureHeader(String fixture);

    public abstract String getModuleHeader(String moduleFunction, String description);

    public abstract String getScriptCodeForWindow(WindowId windowId);

    public abstract String getScriptCodeForWindowClose(WindowId windowId);

    public abstract String getFunctionCallForInsertDialog(Function f, String[] arguments);

    public abstract String[] parseMessage(String msg);

    public abstract String[] getFixtures();

    public abstract boolean isSourceFile(File f);

    public abstract boolean isTestFile(File f);

    public abstract String getSuffix();

    public abstract int getLinePositionForInsertion();

    public abstract String getScriptCodeForShowChecklist(String fileName);

    public abstract void createDefaultFixture(JDialog configurationUI, Properties props, File fixtureDir, List<String> keys);

    public abstract String getScriptCodeForInsertChecklist(String fileName);

    public abstract String getScriptCodeForImportAction(String pkg, String function);

    public abstract String getFunctionFromInsertDialog(String function);

    public abstract String getPackageFromInsertDialog(String function);

    public abstract int getLinePositionForInsertionModule();

    public abstract String updateScriptWithImports(String text, HashSet<String> importStatements);

    public abstract String getDefaultFixtureHeader(Properties props, String launcher, List<String> keys);

    public abstract void fileUpdated(File file, SCRIPT_FILE_TYPE type);

    public abstract String getMarathonStartMarker();

    public abstract String getMarathonEndMarker();

    public abstract String getPlaybackImportStatement();

    public Map<String, Object> getFixtureProperties(String script);

    public abstract Object eval(String script);

    public abstract IScript createScript(Writer scriptOutput, Writer scriptError, String scriptText, String filePath,
            boolean isRecording, boolean isDebugging, Properties dataVariables);

    public abstract String getScriptCodeForGenericAction(String method, String name, Object... params);

}
