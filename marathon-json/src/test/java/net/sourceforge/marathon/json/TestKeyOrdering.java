package net.sourceforge.marathon.json;

import java.util.Iterator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestKeyOrdering {

    @Test
    public void testSimple() throws Exception {
        final JSONObject obj = new JSONObject();
        obj.put("one", 1);
        obj.put("two", 2);
        obj.put("three", 3);
        obj.put("four", 4);
        obj.put("five", 5);
        obj.put("six", 6);

        final Iterator<String> it = obj.keys();
        String key;
        int val;

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "one");
        Assert.assertEquals(val, 1);

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "two");
        Assert.assertEquals(val, 2);

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "three");
        Assert.assertEquals(val, 3);

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "four");
        Assert.assertEquals(val, 4);

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "five");
        Assert.assertEquals(val, 5);

        key = it.next().toString();
        val = obj.getInt(key);
        Assert.assertEquals(key, "six");
        Assert.assertEquals(val, 6);
    }
}
