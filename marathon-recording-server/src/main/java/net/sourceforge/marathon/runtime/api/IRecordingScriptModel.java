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

public interface IRecordingScriptModel {

	public String getScriptCodeForWindow(WindowId windowId);

	public String getScriptCodeForWindowClose(WindowId windowId);

	public String getScriptCodeForShowChecklist(String fileName);

	public String getScriptCodeForInsertChecklist(String fileName);

	public String getScriptCodeForImportAction(String pkg, String function);

	public String getFunctionFromInsertDialog(String function);

	public String getPackageFromInsertDialog(String function);

	public String getScriptCodeForGenericAction(String method, String suffix, String name, Object... params);

}
