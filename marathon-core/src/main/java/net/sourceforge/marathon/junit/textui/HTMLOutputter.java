/*******************************************************************************
 * Copyright 2016 Jalian Systems Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
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
