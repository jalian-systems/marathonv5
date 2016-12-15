/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javafx.stage.Stage;
import net.sourceforge.marathon.runtime.fx.api.ISublayoutProvider;

public interface IScriptModel extends ISublayoutProvider {

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

    public abstract void createDefaultFixture(Stage window, Properties props, File fixtureDir, List<String> keys);

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
            boolean isRecording, boolean isDebugging, Properties dataVariables, String framework);

    public abstract String getScriptCodeForGenericAction(String method, String name, Object... params);

}
