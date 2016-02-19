package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class ClassPathHelper {

    public static String getClassPath(Class<?> klass) {
        String packageName = klass.getPackage().getName();
        return getClassPath(packageName, klass.getName());
    }

    private static String getClassPath(String packageName, String name) {
        name = name.replace('.', '/');
        URL url = ClassPathHelper.class.getResource("/" + name + ".class");
        if (url == null)
            return null;
        String resource = null;
        try {
            resource = URLDecoder.decode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        if (resource.startsWith("jar:"))
            resource = resource.substring(4);
        if (resource.startsWith("file:"))
            resource = resource.substring(5);
        int index = resource.indexOf('!');
        if (index != -1)
            resource = resource.substring(0, index);
        else {
            String packagePath = packageName.replace('.', '/');
            index = resource.indexOf(packagePath);
            if (index != -1)
                resource = resource.substring(0, index - 1);
        }
        if (OSUtils.isWindowsOS()) {
            resource = resource.substring(1);
        }
        return resource.replace('/', File.separatorChar);
    }

    public static String getClassPath(String name) {
        int lastIndexOf = name.lastIndexOf('.');
        String packageName;
        if (lastIndexOf == -1)
            packageName = "";
        else
            packageName = name.substring(0, lastIndexOf);
        return getClassPath(packageName, name);
    }
}
