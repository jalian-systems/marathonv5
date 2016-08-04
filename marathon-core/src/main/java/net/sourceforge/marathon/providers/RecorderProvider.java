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
package net.sourceforge.marathon.providers;

import com.google.inject.Provider;

import net.sourceforge.marathon.recorder.IScriptListener;
import net.sourceforge.marathon.recorder.ScriptRecorder;
import net.sourceforge.marathon.runtime.api.IRecorder;

public class RecorderProvider implements Provider<IRecorder> {

    private IScriptListener scriptListener;

    public void setScriptListener(IScriptListener scriptListener) {
        this.scriptListener = scriptListener;
    }

    @Override public IRecorder get() {
        return new ScriptRecorder(scriptListener);
    }

}
