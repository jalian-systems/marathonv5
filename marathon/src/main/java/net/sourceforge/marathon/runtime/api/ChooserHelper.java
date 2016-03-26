package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;

public class ChooserHelper {

	private static final String homeDir;
	private static final String cwd;
	private static final String marathonDir;

	static {
		homeDir = getRealPath(System.getProperty("user.home", null));
		cwd = getRealPath(System.getProperty("user.dir", null));
		marathonDir = getRealPath(System.getProperty("marathon.project.dir", null));
	}

	private static String getRealPath(String path) {
		if (path == null)
			return "";
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			return "";
		}
	}

	public static String decode(String s) {
		if(s.length() == 0)
			return "";
		JSONArray ja = new JSONArray(s);
		JSONArray r = new JSONArray();
		for (int i = 0; i < ja.length(); i++) {
			String file = ja.getString(i);
			r.put(decodeFile(file));
		}
		return r.toString();
	}

	private static String decodeFile(String file) {
		if (file.startsWith("#M"))
			return file.replace("#M", marathonDir);
		else if (file.startsWith("#C"))
			return file.replace("#C", cwd);
		else if (file.startsWith("#H"))
			return file.replace("#H", homeDir);
		return file;
	}

	public static String encode(File file) {
		String path;
		try {
			path = file.getCanonicalPath();

			String prefix = "";
			if (marathonDir != null && path.startsWith(marathonDir)) {
				prefix = "#M";
				path = path.substring(marathonDir.length());
			} else if (cwd != null && path.startsWith(cwd)) {
				prefix = "#C";
				path = path.substring(cwd.length());
			} else if (homeDir != null && path.startsWith(homeDir)) {
				prefix = "#H";
				path = path.substring(homeDir.length());
			}
			return (prefix + path).replace(File.separatorChar, '/');
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}