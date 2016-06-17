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
package net.sourceforge.marathon.javaagent;

import java.awt.event.KeyEvent;
import java.util.List;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

@Test public class KeyBoardMapTest {

    public void findEntryFora() {
        KeyboardMap kbMap = new KeyboardMap('a');
        List<CharSequence[]> keys = kbMap.getKeys();
        AssertJUnit.assertEquals(1, keys.size());
        CharSequence[] cs = keys.get(0);
        AssertJUnit.assertEquals(1, cs.length);
        AssertJUnit.assertEquals("" + KeyEvent.VK_A, cs[0]);
    }

    public void findEntryForA() {
        KeyboardMap kbMap = new KeyboardMap('A');
        List<CharSequence[]> keys = kbMap.getKeys();
        AssertJUnit.assertEquals(1, keys.size());
        CharSequence[] cs = keys.get(0);
        AssertJUnit.assertEquals(2, cs.length);
        AssertJUnit.assertEquals(JavaAgentKeys.SHIFT, cs[0]);
        AssertJUnit.assertEquals("" + KeyEvent.VK_A, cs[1]);
    }

    public void findEntryForAt() {
        KeyboardMap kbMap = new KeyboardMap('@');
        List<CharSequence[]> keys = kbMap.getKeys();
        AssertJUnit.assertEquals(1, keys.size());
        CharSequence[] cs = keys.get(0);
        AssertJUnit.assertEquals(2, cs.length);
        AssertJUnit.assertEquals(JavaAgentKeys.SHIFT, cs[0]);
        AssertJUnit.assertEquals("" + KeyEvent.VK_2, cs[1]);
    }

}
