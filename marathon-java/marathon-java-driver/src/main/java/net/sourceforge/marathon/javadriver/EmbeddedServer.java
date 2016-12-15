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
package net.sourceforge.marathon.javadriver;

import java.io.IOException;

import fi.iki.elonen.NanoHTTPD;
import net.sourceforge.marathon.javaagent.server.JavaServer;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;

public class EmbeddedServer {

    private NanoHTTPD javaServer;
    private JavaProfile profile;

    public EmbeddedServer(JavaProfile profile) {
        this.profile = profile;
    }

    public void start(int port) throws IOException {
        if (profile.getLaunchType() == LaunchType.SWING_APPLICATION) {
            javaServer = new JavaServer(port);
            javaServer.start();
        } else {
            try {
                javaServer = (NanoHTTPD) Class.forName("net.sourceforge.marathon.javafxagent.server.JavaServer")
                        .getConstructor(Integer.TYPE).newInstance(port);
            } catch (Throwable t) {
                throw new RuntimeException("Unable to instantiate JavaServer", t);
            }
            javaServer.start();
        }
    }

    public void stop() {
        if (javaServer != null) {
            javaServer.stop();
        }
    }

}
