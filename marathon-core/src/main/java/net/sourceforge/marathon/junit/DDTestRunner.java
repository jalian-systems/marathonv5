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
package net.sourceforge.marathon.junit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import net.sourceforge.marathon.runtime.api.Constants;
import net.sourceforge.marathon.runtime.api.IConsole;

public class DDTestRunner {
    
    public static final Logger LOGGER = Logger.getLogger(DDTestRunner.class.getName());

    Pattern pattern = Pattern.compile(".*use_data_file\\s*\\(\\s*\"([^\"]*)\".*$|.*use_data_file\\s*\\(\\s*'([^\']*)'.*$");

    private final String scriptText;
    private final IConsole console;
    private int nTests = 1;

    private String fileName = null;

    private List<String[]> data;

    private String[] header;

    private int currentIndex;

    private String[] currentData;

    int runIndex = 0;

    public DDTestRunner(IConsole console, String scriptText) throws IOException {
        this.console = console;
        this.scriptText = scriptText;
        processForDataFile(scriptText);
    }

    public DDTestRunner(IConsole console, File file) throws IOException {
        this.console = console;
        this.scriptText = getScript(file);
        processForDataFile(scriptText);
    }

    private String getScript(File file) throws IOException {
        int size = (int) file.length();
        char[] cs = new char[size + 64];
        FileReader fileReader = new FileReader(file);
        int n = fileReader.read(cs);
        fileReader.close();
        return new String(cs, 0, n);
    }

    private void processForDataFile(String scriptText) throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(scriptText));
        String line;
        while ((line = br.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                fileName = matcher.group(2);
                readCSVData();
                return;
            }
        }
    }

    private void readCSVData() throws IOException {
        File dataFile = new File(fileName);
        if (!dataFile.exists()) {
            File dataDir = new File(System.getProperty(Constants.PROP_PROJECT_DIR), "TestData");
            dataFile = new File(dataDir, fileName);
        }
        CSVReader reader = null;
        try {
            reader = new CSVReader(new FileReader(dataFile));
            data = reader.readAll();
            if (data == null || data.size() < 2) {
                throw new IllegalArgumentException("No data in CSV file?");
            }
            header = data.get(0);
            currentIndex = 1;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public String getScriptText() {
        return scriptText;
    }

    public IConsole getConsole() {
        return console;
    }

    public boolean hasNext() {
        if (fileName == null) {
            return nTests-- > 0;
        }
        return csvHasNext();
    }

    private boolean csvHasNext() {
        while (currentIndex < data.size()) {
            String[] datum = data.get(currentIndex);
            if (datum.length > 1 || datum.length == 1 && !"".equals(datum[0])) {
                break;
            }
            currentIndex++;
        }
        return currentIndex < data.size();
    }

    public void next() {
        if (fileName != null) {
            currentData = data.get(currentIndex);
            currentIndex++;
        }
    }

    public Properties getDataVariables() {
        Properties props = new Properties();
        if (fileName == null) {
            return props;
        }
        for (int i = 0; i < Math.min(currentData.length, header.length); i++) {
            props.put(header[i], makeString(currentData[i]));
        }
        props.setProperty("csv_index", currentIndex + "");
        return props;
    }

    private String makeString(String string) {
        if (string.startsWith("\"") && string.endsWith("\"")) {
            return string;
        }
        if (string.startsWith("'") && string.endsWith("'")) {
            return string;
        }
        if (isNumber(string)) {
            return string;
        }
        return "\"" + string + "\"";
    }

    private boolean isNumber(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            try {
                Float.parseFloat(string);
                return true;
            } catch (NumberFormatException e1) {
                try {
                    Double.parseDouble(string);
                    return true;
                } catch (NumberFormatException e2) {
                }
            }
        }
        return false;
    }

    public boolean isDDT() {
        return fileName != null;
    }

    public String getName() {
        if (!isDDT()) {
            return "";
        }
        if (header[0].equals("marathon_test_name")) {
            return "[" + currentData[0] + "]";
        }
        if (runIndex == 0) {
            runIndex = 2;
            return "";
        }
        return "[" + runIndex++ + "]";
    }
}
