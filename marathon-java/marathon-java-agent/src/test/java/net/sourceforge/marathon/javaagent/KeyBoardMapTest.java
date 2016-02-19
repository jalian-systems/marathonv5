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
