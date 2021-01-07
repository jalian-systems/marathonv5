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
package net.sourceforge.marathon.javafxrecorder;

import java.awt.Component;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JWindow;

import org.testng.AssertJUnit;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.sourceforge.marathon.json.JSONObject;

@Test
public class JSONOMapConfigTest {
    // @formatter:off
    public static final String JSONOMapConfiguration =
            "{" +
                    "  \"generalProperties\": [" +
                    "    \"labeledBy\"," +
                    "    \"toolTipText\"," +
                    "    \"name\"," +
                    "    \"labelText\"," +
                    "    \"precedingLabel\"," +
                    "    \"cText\"," +
                    "    \"iconFile\"," +
                    "    \"position\"," +
                    "    \"size\"," +
                    "    \"accelerator\"," +
                    "    \"enabled\"," +
                    "    \"toolTipText\"," +
                    "    \"fieldName\"," +
                    "    \"layoutData.gridx\"," +
                    "    \"layoutData.gridy\"," +
                    "    \"layoutData.x\"," +
                    "    \"layoutData.y\"," +
                    "    \"accessibleContext.accessibleName\"" +
                    "  ]," +
                    "  \"configFile\": \"/Users/dakshinamurthykarra/Projects/marathon-projects/scriptmodel-rewrite-test/omap-configuration.yaml\"," +
                    "  \"recognitionProperties\": [{" +
                    "    \"propertyLists\": [" +
                    "      {" +
                    "        \"priority\": 100," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"name\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 1971244528" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 90," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"fieldName\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 1476559827" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 80," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"actionCommand\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 219987657" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 70," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"buttonText\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 1513194712" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 60," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"labeledBy\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 1762176810" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 50," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"accessibleName\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 958669893" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 40," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"buttonIconFile\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 1942571857" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 30," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"precedingLabel\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 611315258" +
                    "      }," +
                    "      {" +
                    "        \"priority\": 20," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"toolTipText\"," +
                    "          \"type\"" +
                    "        ]," +
                    "        \"hCode\": 813773958" +
                    "      }" +
                    "    ]," +
                    "    \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "    \"className\": \"java.awt.Component\"," +
                    "    \"hCode\": 1923048010" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 100," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [\"labelText\", \"type\" ]," +
                    "        \"hCode\": 1148226303" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JLabel\"," +
                    "      \"hCode\": 951422579" +
                    "  }]," +
                    "  \"containerNamingProperties\": [" +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 100," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [\"title\"]," +
                    "        \"hCode\": 1039016720" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"java.awt.Window\"," +
                    "      \"hCode\": 1564479283" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 100," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [" +
                    "          \"title\"," +
                    "          \"internalFrameIndex2\"" +
                    "        ]," +
                    "        \"hCode\": 413535513" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JInternalFrame\"," +
                    "      \"hCode\": 1034285299" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 50," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [\"component.class.simpleName\"]," +
                    "        \"hCode\": 1500381475" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JWindow\"," +
                    "      \"hCode\": 1788497806" +
                    "    }" +
                    "  ]," +
                    "  \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration\"," +
                    "  \"containerRecognitionProperties\": [" +
                    "    {" +
                    "      \"propertyLists\": [" +
                    "        {" +
                    "          \"priority\": 100," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"oMapClassName\"]," +
                    "          \"hCode\": 954018383" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 90," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [" +
                    "            \"component.class.name\"," +
                    "            \"title\"" +
                    "          ]," +
                    "          \"hCode\": 1074054579" +
                    "        }" +
                    "      ]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"java.awt.Window\"," +
                    "      \"hCode\": 2035256218" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [" +
                    "        {" +
                    "          \"priority\": 100," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"oMapClassName\"]," +
                    "          \"hCode\": 1431726510" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 90," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [" +
                    "            \"component.class.name\"," +
                    "            \"title\"" +
                    "          ]," +
                    "          \"hCode\": 993139181" +
                    "        }" +
                    "      ]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JInternalFrame\"," +
                    "      \"hCode\": 1447310583" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 50," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [\"component.class.simpleName\"]," +
                    "        \"hCode\": 401698912" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JWindow\"," +
                    "      \"hCode\": 1812511463" +
                    "    }" +
                    "  ]," +
                    "  \"namingProperties\": [" +
                    "    {" +
                    "      \"propertyLists\": [" +
                    "        {" +
                    "          \"priority\": 100," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"buttonText\"]," +
                    "          \"hCode\": 824625946" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 80," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"accessibleName\"]," +
                    "          \"hCode\": 2109486170" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 70," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"buttonIconFile\"]," +
                    "          \"hCode\": 1326530867" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 90," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"labeledBy\"]," +
                    "          \"hCode\": 1776300331" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 60," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"precedingLabel\"]," +
                    "          \"hCode\": 1976542162" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 50," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"toolTipText\"]," +
                    "          \"hCode\": 319581291" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 40," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"name\"]," +
                    "          \"hCode\": 346156690" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 30," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"fieldName\"]," +
                    "          \"hCode\": 322289107" +
                    "        }," +
                    "        {" +
                    "          \"priority\": 20," +
                    "          \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "          \"properties\": [\"actionCommand\"]," +
                    "          \"hCode\": 759263615" +
                    "        }" +
                    "      ]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"java.awt.Component\"," +
                    "      \"hCode\": 595746831" +
                    "    }," +
                    "    {" +
                    "      \"propertyLists\": [{" +
                    "        \"priority\": 100," +
                    "        \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$PropertyList\"," +
                    "        \"properties\": [\"labelText\"]," +
                    "        \"hCode\": 1148226303" +
                    "      }]," +
                    "      \"class\": \"net.sourceforge.marathon.objectmap.ObjectMapConfiguration$ObjectIdentity\"," +
                    "      \"className\": \"javax.swing.JLabel\"," +
                    "      \"hCode\": 951422579" +
                    "    }" +
                    "  ]," +
                    "  \"hCode\": 396049691" +
                    "}" ;

    // @formatter:on

    private JSONOMapConfig config;

    @BeforeClass
    public void beforeClass() {
        config = new JSONOMapConfig(new JSONObject(JSONOMapConfiguration));
    }

    @AfterClass
    public void afterClass() {
    }

    public void collectProperties() {
        Collection<String> allProps = config.findProperties();
        AssertJUnit.assertEquals(29, allProps.size());
        AssertJUnit.assertTrue(allProps.contains("labelText"));
        AssertJUnit.assertTrue(allProps.contains("actionCommand"));
        AssertJUnit.assertTrue(allProps.contains("component.class.simpleName"));
    }

    public void findRP() {
        List<List<String>> rp = config.findRP(JTextField.class);
        AssertJUnit.assertEquals(9, rp.size());
        rp = config.findRP(JLabel.class);
        AssertJUnit.assertEquals(10, rp.size());
    }

    public void findContainerRP() {
        List<List<String>> rp = config.findContainerRP(Window.class);
        AssertJUnit.assertEquals(2, rp.size());
        rp = config.findContainerRP(JInternalFrame.class);
        AssertJUnit.assertEquals(2, rp.size());
    }

    public void findNP() {
        List<List<String>> rp = config.findNP(JTextField.class);
        AssertJUnit.assertEquals(9, rp.size());
        rp = config.findNP(JLabel.class);
        AssertJUnit.assertEquals(10, rp.size());
    }

    public void findContainerNP() {
        List<List<String>> np = config.findContainerNP(Window.class);
        AssertJUnit.assertEquals(1, np.size());
        np = config.findContainerNP(JInternalFrame.class);
        AssertJUnit.assertEquals(1, np.size());
        np = config.findContainerNP(JWindow.class);
        AssertJUnit.assertEquals(2, np.size());
    }

    public void sortProperties() {
        List<JSONObject> os = new ArrayList<JSONObject>();
        os.add(createObj(80, JLabel.class));
        os.add(createObj(100, Component.class));
        os.add(createObj(80, Component.class));
        os.add(createObj(90, JLabel.class));

        Collections.sort(os, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int o2prio = o2.getInt("priority");
                int o1prio = o1.getInt("priority");
                if (o1prio == o2prio) {
                    Class<?> o1class = (Class<?>) o1.get("class");
                    Class<?> o2class = (Class<?>) o2.get("class");
                    if (o1class.isAssignableFrom(o2class)) {
                        return 1;
                    }
                    return -1;
                }
                return o2prio - o1prio;
            }
        });

    }

    private JSONObject createObj(int prio, Class<? extends Component> klass) {
        JSONObject o1 = new JSONObject();
        o1.put("priority", prio);
        o1.put("properties", "props-" + prio);
        o1.put("class", klass);
        return o1;
    }
}
