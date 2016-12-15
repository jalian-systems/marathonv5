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
package net.sourceforge.marathon.resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.marathon.runtime.api.ScriptModel;

public class Project {

    private Watcher watcher;
    private static String suffix = ScriptModel.getModel().getSuffix();

    public Project() {
        init();
    }

    private void init() {
        watcher = new Watcher();
    }

    public void refresh() {
        watcher.stop();
        init();
    }

    private static final Pattern testPattern = Pattern.compile("^\\s*def\\s*test\\s*$");
    private static final Pattern NamePattern1 = Pattern.compile("^\\s*name\\s*\\(\\s*'([^']*)'\\s*\\).*$");
    private static final Pattern NamePattern2 = Pattern.compile("^\\s*name\\s*\\(\\s*\"([^\"]*)\"\\s*\\).*$");
    private static final Pattern DescriptionPattern1 = Pattern.compile("^\\s*description\\s*\\(\\s*'([^']*)'\\s*\\).*$");
    private static final Pattern DescriptionPattern2 = Pattern.compile("^\\s*description\\s*\\(\\s*\"([^\"]*)\"\\s*\\).*$");
    private static final Pattern SeverityPattern1 = Pattern.compile("^\\s*severity\\s*\\(\\s*'([^']*)'\\s*\\).*$");
    private static final Pattern SeverityPattern2 = Pattern.compile("^\\s*severity\\s*\\(\\s*\"([^\"]*)\"\\s*\\).*$");
    private static final Pattern IDPattern1 = Pattern.compile("^\\s*id\\s*\\(\\s*'([^']*)'\\s*\\).*$");
    private static final Pattern IDPattern2 = Pattern.compile("^\\s*id\\s*\\(\\s*\"([^\"]*)\"\\s*\\).*$");

    public static String getTestName(File file) {
        String name = null;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                Matcher matcher = NamePattern1.matcher(line);
                if (matcher.matches()) {
                    name = matcher.group(1);
                } else {
                    matcher = NamePattern2.matcher(line);
                    if (matcher.matches()) {
                        name = matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Project.getTestName( " + e.getMessage() + " )");
            e.printStackTrace();
        }
        if (name == null) {
            name = file.getName();
        }

        if (name.endsWith(suffix)) {
            name = name.substring(0, name.length() - suffix.length());
        }
        return name;
    }

    public static Properties getTestProperties(File file) {
        Properties props = new Properties();
        Map<String, List<Pattern>> lookfor = new HashMap<>();
        lookfor.put("name", Arrays.asList(NamePattern1, NamePattern2));
        lookfor.put("description", Arrays.asList(DescriptionPattern1, DescriptionPattern2));
        lookfor.put("severity", Arrays.asList(SeverityPattern1, SeverityPattern2));
        lookfor.put("id", Arrays.asList(IDPattern1, IDPattern2));
        lookForMatches(file, lookfor, props);
        if (!props.containsKey("name")) {
            String name = file.getName();
            if (name.endsWith(suffix)) {
                name = name.substring(0, name.length() - suffix.length());
            }
            props.put("name", name);
        }
        return props;
    }

    private static void lookForMatches(File file, Map<String, List<Pattern>> lookfor, Properties props) {
        if (!file.exists())
            return;
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                if (testPattern.matcher(line).matches()) {
                    break;
                }
                for (String key : lookfor.keySet()) {
                    if (props.containsKey(key)) {
                        continue;
                    }
                    String matched = matchForPattern(line, lookfor.get(key));
                    if (matched != null) {
                        props.put(key, matched);
                        break;
                    }
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String matchForPattern(String line, List<Pattern> patterns) {
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    public static void setTestName(String name, File file) {
        int nameLine = -1;
        int descriptionLine = -1;
        int severityLine = -1;
        int idLine = -1;
        int fallbackLine = -1;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String s = lines.get(i);
                if (NamePattern1.matcher(s).matches()) {
                    nameLine = i;
                    break;
                }
                if (NamePattern2.matcher(s).matches()) {
                    nameLine = i;
                    break;
                }
            }
            if (name == null || "".equals(name)) {
                if (nameLine != -1) {
                    removeLine(file, nameLine, lines);
                }
                return;
            }
            if (nameLine == -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String s = lines.get(i);
                    if (DescriptionPattern1.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (DescriptionPattern2.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (SeverityPattern1.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (SeverityPattern2.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (IDPattern1.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (IDPattern2.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (testPattern.matcher(s).matches()) {
                        fallbackLine = i;
                    }
                }
                if (descriptionLine != -1) {
                    lines.add(descriptionLine - 1, new String());
                    nameLine = descriptionLine;
                } else if (severityLine != -1) {
                    lines.add(severityLine - 1, new String());
                    System.out.println("Project.setTestName() " + (severityLine - 1));
                    nameLine = severityLine;
                } else if (idLine != -1) {
                    lines.add(idLine - 1, new String());
                    nameLine = idLine;
                } else {
                    if (fallbackLine == -1) {
                        throw new IOException("Could not find `def test` or `name()`");
                    }
                    lines.add(fallbackLine, new String());
                    lines.add(fallbackLine, new String());
                    nameLine = fallbackLine;
                }
            }
            lines.set(nameLine, new String("name('" + name + "')"));
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTestDescription(File file) {
        String description = null;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                Matcher matcher = DescriptionPattern1.matcher(line);
                if (matcher.matches()) {
                    description = matcher.group(1);
                } else {
                    matcher = DescriptionPattern2.matcher(line);
                    if (matcher.matches()) {
                        description = matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Project.getTestDescription( " + e.getMessage() + " )");
            e.printStackTrace();
        }
        if (description != null) {
            description = description.replaceAll("\\\\n", "\n");
            description = description.replaceAll("\\\\r", "\r");
        }
        return description;
    }

    public static void setTestDescription(String description, File file) {
        int nameLine = -1;
        int descriptionLine = -1;
        int severityLine = -1;
        int idLine = -1;
        int fallbackLine = -1;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String s = lines.get(i);
                if (DescriptionPattern1.matcher(s).matches()) {
                    descriptionLine = i;
                    break;
                }
                if (DescriptionPattern2.matcher(s).matches()) {
                    descriptionLine = i;
                    break;
                }
            }
            if (description == null || "".equals(description)) {
                if (descriptionLine != -1) {
                    removeLine(file, descriptionLine, lines);
                }
                return;
            }
            if (descriptionLine == -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String s = lines.get(i);
                    if (NamePattern1.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (NamePattern2.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (SeverityPattern1.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (SeverityPattern2.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (IDPattern1.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (IDPattern2.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (testPattern.matcher(s).matches()) {
                        fallbackLine = i;
                    }
                }
                if (nameLine != -1) {
                    lines.add(nameLine + 1, new String());
                    descriptionLine = nameLine + 1;
                } else if (severityLine != -1) {
                    lines.add(severityLine - 1, new String());
                    descriptionLine = severityLine;
                } else if (idLine != -1) {
                    lines.add(idLine - 1, new String());
                    descriptionLine = idLine;
                } else {
                    if (fallbackLine == -1) {
                        throw new IOException("Could not find `def test` or `description()`");
                    }
                    lines.add(fallbackLine, new String());
                    lines.add(fallbackLine, new String());
                    descriptionLine = fallbackLine;
                }
            }
            description = inspect(description);
            lines.set(descriptionLine, new String("description('" + description + "')"));
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTestSeverity(File file) {
        String severity = null;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                Matcher matcher = SeverityPattern1.matcher(line);
                if (matcher.matches()) {
                    severity = matcher.group(1);
                } else {
                    matcher = SeverityPattern2.matcher(line);
                    if (matcher.matches()) {
                        severity = matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Project.getTestSeverity( " + e.getMessage() + " )");
            e.printStackTrace();
        }
        return severity;
    }

    public static void setTestSeverity(String severity, File file) {
        int nameLine = -1;
        int descriptionLine = -1;
        int severityLine = -1;
        int idLine = -1;
        int fallbackLine = -1;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String s = lines.get(i);
                if (SeverityPattern1.matcher(s).matches()) {
                    severityLine = i;
                    break;
                }
                if (SeverityPattern2.matcher(s).matches()) {
                    severityLine = i;
                    break;
                }
            }
            if (severity == null || "".equals(severity)) {
                if (severityLine != -1) {
                    removeLine(file, severityLine, lines);
                }
                return;
            }
            if (severityLine == -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String s = lines.get(i);
                    if (DescriptionPattern1.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (DescriptionPattern2.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (IDPattern1.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (IDPattern2.matcher(s).matches()) {
                        idLine = i;
                    }
                    if (NamePattern1.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (NamePattern2.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (testPattern.matcher(s).matches()) {
                        fallbackLine = i;
                    }
                }
                if (descriptionLine != -1) {
                    lines.add(descriptionLine + 1, new String());
                    severityLine = descriptionLine + 1;
                } else if (idLine != -1) {
                    lines.add(idLine - 1, new String());
                    severityLine = idLine;
                } else if (nameLine != -1) {
                    lines.add(nameLine + 1, new String());
                    severityLine = nameLine + 1;
                } else {
                    if (fallbackLine == -1) {
                        throw new IOException("Could not find `def test` or `severity()`");
                    }
                    lines.add(fallbackLine, new String());
                    lines.add(fallbackLine, new String());
                    severityLine = fallbackLine;
                }
            }
            lines.set(severityLine, new String("severity('" + severity + "')"));
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTestID(File file) {
        String id = null;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                Matcher matcher = IDPattern1.matcher(line);
                if (matcher.matches()) {
                    id = matcher.group(1);
                } else {
                    matcher = IDPattern2.matcher(line);
                    if (matcher.matches()) {
                        id = matcher.group(1);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Project.getTestID( " + e.getMessage() + " )");
            e.printStackTrace();
        }
        return id;
    }

    public static void setTestID(String id, File file) {
        int nameLine = -1;
        int descriptionLine = -1;
        int severityLine = -1;
        int idLine = -1;
        int fallbackLine = -1;
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 0; i < lines.size(); i++) {
                String s = lines.get(i);
                if (IDPattern1.matcher(s).matches()) {
                    idLine = i;
                    break;
                }
                if (IDPattern2.matcher(s).matches()) {
                    idLine = i;
                    break;
                }
            }
            if (id == null || "".equals(id)) {
                if (idLine != -1) {
                    removeLine(file, idLine, lines);
                }
                return;
            }
            if (idLine == -1) {
                for (int i = 0; i < lines.size(); i++) {
                    String s = lines.get(i);
                    if (SeverityPattern1.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (SeverityPattern2.matcher(s).matches()) {
                        severityLine = i;
                    }
                    if (DescriptionPattern1.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (DescriptionPattern2.matcher(s).matches()) {
                        descriptionLine = i;
                    }
                    if (NamePattern1.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (NamePattern2.matcher(s).matches()) {
                        nameLine = i;
                    }
                    if (testPattern.matcher(s).matches()) {
                        fallbackLine = i;
                    }
                }
                if (severityLine != -1) {
                    lines.add(severityLine + 1, new String());
                    idLine = severityLine + 1;
                } else if (descriptionLine != -1) {
                    lines.add(descriptionLine + 1, new String());
                    idLine = descriptionLine + 1;
                } else if (nameLine != -1) {
                    lines.add(nameLine + 1, new String());
                    idLine = nameLine + 1;
                } else {
                    if (fallbackLine == -1) {
                        throw new IOException("Could not find `def test` or `id()`");
                    }
                    lines.add(fallbackLine, new String());
                    lines.add(fallbackLine, new String());
                    idLine = fallbackLine;
                }
            }
            lines.set(idLine, new String("id('" + id + "')"));
            Files.write(file.toPath(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeLine(File file, int lineNumber, List<String> lines) throws IOException {
        String previousLine = lines.get(lineNumber - 1);
        lines.remove(lineNumber);
        String nextLine = lines.get(lineNumber);
        if ("".equals(previousLine) && "".equals(nextLine)) {
            lines.remove(lineNumber);
        } else if ("".equals(nextLine) && !testPattern.matcher(lines.get(lineNumber + 1)).matches()) {
            lines.remove(lineNumber);
        }
        Files.write(file.toPath(), lines);
    }

    public static String inspect(String string) {
        StringBuilder sb = new StringBuilder();
        char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '"' || c == '\\') {
                sb.append("\\").append(c);
            } else if (c == '#' && chars[i + 1] == '{') {
                sb.append("\\").append(c);
            } else if (c == '\n') {
                sb.append("\\").append('n');
            } else if (c == '\r') {
                sb.append("\\").append('r');
            } else if (c == '\t') {
                sb.append("\\").append('t');
            } else if (c == '\f') {
                sb.append("\\").append('f');
            } else if (c == '\013') {
                sb.append("\\").append('v');
            } else if (c == '\010') {
                sb.append("\\").append('b');
            } else if (c == '\007') {
                sb.append("\\").append('a');
            } else if (c == '\033') {
                sb.append("\\").append('e');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
