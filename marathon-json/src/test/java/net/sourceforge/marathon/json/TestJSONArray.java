package net.sourceforge.marathon.json;

import org.testng.annotations.Test;

public class TestJSONArray {

    @Test
    public void testEmptyArray() {
        new JSONArray("{}");
    }

}
