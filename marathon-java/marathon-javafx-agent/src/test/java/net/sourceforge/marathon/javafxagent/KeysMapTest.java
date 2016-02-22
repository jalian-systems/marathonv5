package net.sourceforge.marathon.javafxagent;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

import java.awt.event.KeyEvent;

import org.testng.annotations.Test;

import net.sourceforge.marathon.javafxagent.JavaAgentKeys;
import net.sourceforge.marathon.javafxagent.KeysMap;

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
