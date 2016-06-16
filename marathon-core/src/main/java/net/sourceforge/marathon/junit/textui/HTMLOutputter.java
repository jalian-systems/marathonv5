package net.sourceforge.marathon.junit.textui;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import junit.framework.Test;

public class HTMLOutputter extends XMLOutputter {
    private Transformer transformer;

    public HTMLOutputter() {
        super();
        TransformerFactory factory = TransformerFactory.newInstance();
        InputStream xsltStream = getClass().getClassLoader().getResourceAsStream("report.xsl");
        try {
            transformer = factory.newTransformer(new StreamSource(xsltStream));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void output(Writer writer, Test testSuite, Map<Test, MarathonTestResult> testOutputMap) {
        StringWriter xmlWriter = new StringWriter();
        super.output(xmlWriter, testSuite, testOutputMap);
        StringReader reader = new StringReader(xmlWriter.toString());
        if (transformer != null) {
            try {
                transformer.transform(new StreamSource(reader), new StreamResult(writer));
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }
}
