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
package net.sourceforge.marathon.resource;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import net.sourceforge.marathon.resource.navigator.FolderResource;

public class Watcher {

    private WatchService watchService;
    private Map<WatchKey, FolderResource> monitored = new HashMap<>();

    private class WatchServiceThread extends Thread {
        private WatchService watchService;

        public WatchServiceThread(WatchService watchService) {
            super("WatchService Thread");
            setDaemon(true);
            this.watchService = watchService;
        }

        @Override public void run() {

            for (;;) {
                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (Exception x) {
                    return;
                }

                FolderResource dir = monitored.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    Kind<?> kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        // TODO: inform overflow handler
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path name = ev.context();

                    // if directory is created, and watching recursively, then
                    // register it and its sub-directories
                    if (kind == ENTRY_CREATE) {
                        Platform.runLater(() -> dir.create(name));
                    }
                    if (kind == ENTRY_DELETE) {
                        Platform.runLater(() -> dir.delete(name));
                    }
                    if (kind == ENTRY_MODIFY) {
                    }
                }

                boolean valid = key.reset();
                if (!valid) {
                    monitored.remove(key);

                    if (monitored.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    public Watcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
            new WatchServiceThread(watchService).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (watchService == null) {
            return;
        }
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(FolderResource folder, Path path) {
        try {
            WatchKey watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            monitored.put(watchKey, folder);
        } catch (IOException e) {
        }
    }

}
