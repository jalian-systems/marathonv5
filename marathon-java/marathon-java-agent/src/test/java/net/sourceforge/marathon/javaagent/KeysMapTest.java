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
package net.sourceforge.marathon.javaagent;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.awt.event.KeyEvent;

import org.testng.annotations.Test;

@Test public class KeysMapTest {

    public void findEntry() {
        KeysMap map = KeysMap.findMap(JavaAgentKeys.F1);
        assertEquals(KeyEvent.VK_F1, map.getCode());
    }

    public void findKeyByUnicode() {
        JavaAgentKeys keyFromUnicode = JavaAgentKeys.getKeyFromUnicode(JavaAgentKeys.F1.charAt(0));
        assertEquals(JavaAgentKeys.F1, keyFromUnicode);
        keyFromUnicode = JavaAgentKeys.getKeyFromUnicode('A');
        assertNull(keyFromUnicode);
    }
}
