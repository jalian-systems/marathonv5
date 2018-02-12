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
package net.sourceforge.marathon.recorder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javafx.application.Platform;
import net.sourceforge.marathon.runtime.api.IRecorder;
import net.sourceforge.marathon.runtime.api.IScriptElement;
import net.sourceforge.marathon.runtime.api.TagInserter;
import net.sourceforge.marathon.runtime.api.WindowId;

public class ScriptRecorder implements IRecorder {

    public static final Logger LOGGER = Logger.getLogger(ScriptRecorder.class.getName());

    private IScriptListener scriptListener;
    private TagInserter tagInserter = new TagInserter();

    private final BlockingQueue<RecordEvent> recordEvents = new LinkedBlockingQueue<RecordEvent>();

    private static class RecordEvent {
        IScriptElement recordable;

        public RecordEvent(IScriptElement recordable) {
            this.recordable = recordable;
        }

        public IScriptElement getRecordable() {
            return recordable;
        }
    }

    protected final Runnable processRecordEventsRunnable = new Runnable() {
        @Override public void run() {
            RecordEvent evt;

            while ((evt = recordEvents.poll()) != null) {
                IScriptElement recordable = evt.getRecordable();
                WindowId windowId = recordable.getWindowId();
                if (windowId == null) {
                    tagInserter.add(recordable);
                } else {
                    windowId.addToTagInserter(tagInserter, recordable);
                }
                updateScript();
            }
        }
    };

    public ScriptRecorder(IScriptListener scriptListener) {
        this.scriptListener = scriptListener;
    }

    @Override public void record(IScriptElement recordable) {
        recordEvents.add(new RecordEvent(recordable));
        if (recordEvents.size() == 1) {
            runProcessRecordEvents();
        }
    }

	protected void runProcessRecordEvents() {
		Platform.runLater(processRecordEventsRunnable);
	}

    @Override public void updateScript() {
        if (scriptListener != null) {
            synchronized (scriptListener) {
                scriptListener.setScript(toScriptCode());
            }
        }
    }

    private String toScriptCode() {
        return tagInserter.getRootTag().toScriptCode();
    }

    @Override public void abortRecording() {
        scriptListener.abortRecording();
    }

    @Override public void insertChecklist(String name) {
        scriptListener.insertChecklistAction(name);
    }

    @Override public String recordInsertScriptElement(WindowId windowId, String function) {
        InsertScriptElement recordable = new InsertScriptElement(windowId, function);
        record(recordable);
        String ims = recordable.getImportStatement();
        if (scriptListener != null) {
            scriptListener.addImportStatement(ims);
        }
        return ims;
    }

    @Override public void recordInsertChecklistElement(WindowId topWindowId, String fileName) {
        record(new InsertChecklistElement(topWindowId, fileName));
    }

    @Override public void recordShowChecklistElement(WindowId windowId, String fileName) {
        record(new ShowChecklistElement(windowId, fileName));
    }

    @Override public boolean isCreatingObjectMap() {
        return false;
    }
}
