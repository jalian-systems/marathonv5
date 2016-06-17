/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package net.sourceforge.marathon.display;

import java.io.File;

import javax.swing.event.EventListenerList;

import net.sourceforge.marathon.navigator.IFileEventListener;

public class FileEventHandler {
    private EventListenerList listeners = new EventListenerList();

    public void addFileEventListener(IFileEventListener fileEventListener) {
        listeners.add(IFileEventListener.class, fileEventListener);
    }

    public void fireRenameEvent(File from, File to) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileRenamed(from, to);
        }
    }

    public void fireDeleteEvent(File file) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileDeleted(file);
        }
    }

    public void fireCopyEvent(File from, File to) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileCopied(from, to);
        }
    }

    public void fireMoveEvent(File from, File to) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileMoved(from, to);
        }
    }

    public void fireNewEvent(File file, boolean openInEditor) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileCreated(file, openInEditor);
        }
    }

    public void fireUpdateEvent(File file) {
        IFileEventListener[] la = listeners.getListeners(IFileEventListener.class);
        for (IFileEventListener l : la) {
            l.fileUpdated(file);
        }
    }
}
