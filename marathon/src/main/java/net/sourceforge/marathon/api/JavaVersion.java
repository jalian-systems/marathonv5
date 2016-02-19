package net.sourceforge.marathon.api;


public class JavaVersion {

    private static String version = System.getProperty("java.version");

    public static boolean atLeast(String target) {
        int[] current = makeParts(version);
        int[] expected = makeParts(target);
        for(int i = 0; i < 4; i++) {
            if(expected[i] > current[i])
                return false;
        }
        return true;
    }

    private static int[] makeParts(String v) {
        int[] r = new int[] { 0, 0, 0, 0 };
        String[] parts = v.split("\\.");
        if (parts.length > 0)
            r[0] = Integer.parseInt(parts[0]);
        if (parts.length > 1)
            r[1] = Integer.parseInt(parts[1]);
        if (parts.length > 2) {
            String[] minors = parts[2].split("_");
            if (minors.length > 0)
                r[2] = Integer.parseInt(minors[0]);
            if (minors.length > 1)
                r[3] = Integer.parseInt(minors[1]);
        }
        return r ;
    }

}
