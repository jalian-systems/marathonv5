package net.sourceforge.marathon.json;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestJsonObject {

    @Test
    public void testIgnoreComments() throws Exception {
        final JSONObject obj = new JSONObject("// this is a comment\n{\"a\":200}");

        Assert.assertEquals(1, obj.length());
        Assert.assertTrue(obj.has("a"));
        Assert.assertEquals(obj.getInt("a"), 200);
    }
}
