package net.sourceforge.marathon.runtime.api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import au.com.bytecode.opencsv.CSVReader;

public class DataReader {

    private String[] header;
    private CSVReader reader;
    private final IScript script;

    public DataReader(String fileName, IScript script) throws IOException {
        this.script = script;
        File dataFile = new File(fileName);
        if (!dataFile.exists()) {
            File dataDir = new File(System.getProperty(Constants.PROP_PROJECT_DIR), "TestData");
            dataFile = new File(dataDir, fileName);
        }
        reader = new CSVReader(new FileReader(dataFile));
        header = reader.readNext();
    }

    public boolean readNext() throws IOException {
        Properties dataVariables = getDataVariables();
        if (dataVariables == null)
            return false;
        script.setDataVariables(dataVariables);
        return true;
    }

    private Properties getDataVariables() throws IOException {
        String[] datum = reader.readNext();
        while (datum != null) {
            if (datum.length > 1 || (datum.length == 1 && !"".equals(datum[0]))) {
                return createVariableMap(datum);
            }
        }
        return null;
    }

    private Properties createVariableMap(String[] datum) {
        Properties props = new Properties();
        for (int i = 0; i < Math.min(datum.length, header.length); i++) {
            props.put(header[i], makeString(datum[i]));
        }
        return props;
    }

    private String makeString(String string) {
        if (string.startsWith("\"") && string.endsWith("\""))
            return string;
        if (string.startsWith("'") && string.endsWith("'"))
            return string;
        if (isNumber(string))
            return string;
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

}
