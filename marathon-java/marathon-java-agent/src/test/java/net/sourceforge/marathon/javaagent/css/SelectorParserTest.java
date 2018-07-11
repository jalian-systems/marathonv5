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
package net.sourceforge.marathon.javaagent.css;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import net.sourceforge.marathon.testhelpers.MissingException;

@Test
public class SelectorParserTest {

    public void parseTag() throws Throwable {
        SelectorParser parser = new SelectorParser("menu");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu", selector.toString());
    }

    public void parseHiphenatedTag() throws Throwable {
        SelectorParser parser = new SelectorParser("menu-bar");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu-bar", selector.toString());
    }

    public void parseTagWithID() throws Throwable {
        SelectorParser parser = new SelectorParser("menu#mainmenu");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu#mainmenu", selector.toString());
    }

    public void parseTagWithIDAsString() throws Throwable {
        SelectorParser parser = new SelectorParser("menu#'main menu'");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu#\"main menu\"", selector.toString());
    }

    public void parseID() throws Throwable {
        SelectorParser parser = new SelectorParser("#mainmenu");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("*#mainmenu", selector.toString());
    }

    public void parseTagWithPseudoSelectorWithNoArgs() throws Throwable {
        SelectorParser parser = new SelectorParser("menu:enabled");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu:enabled", selector.toString());
    }

    public void parseTagWithPseudoSelectorWithArgs() throws Throwable {
        SelectorParser parser = new SelectorParser("menu#mainmenu:has-text('Hello World'):enabled");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu#mainmenu:has-text(\"Hello World\"):enabled", selector.toString());
    }

    public void parseTagWithBooleanArgs() throws Throwable {
        SelectorParser parser = new SelectorParser("menu:enabled(true)");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu:enabled(true)", selector.toString());
    }

    public void parseTagWithNumberArgs() throws Throwable {
        SelectorParser parser = new SelectorParser("menu:has-size(50, 60.9)");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu:has-size(50, 60.9)", selector.toString());
    }

    public void parseTagWithBooleanAttributes() throws Throwable {
        SelectorParser parser = new SelectorParser("menu[enabled=true]");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu[enabled = true]", selector.toString());
    }

    public void parseTagWithStringAttributes() throws Throwable {
        SelectorParser parser = new SelectorParser("menu[text='File']");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu[text = \"File\"]", selector.toString());
    }

    public void parseTagWithNestedAttributes() throws Throwable {
        SelectorParser parser = new SelectorParser("menu[text.name='File']");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu[text.name = \"File\"]", selector.toString());
    }

    public void parseTagWithIntegerAttributes() throws Throwable {
        SelectorParser parser = new SelectorParser("menu[size=60]");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu[size = 60]", selector.toString());
    }

    public void parseTagWithStringAttributesDifferentOps() throws Throwable {
        SelectorParser parser = new SelectorParser("menu[enabled][text='File'][accel*='K'][class^='javax'][class$='menu']");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals(
                "menu[enabled][text = \"File\"][accel contains \"K\"][class startsWith \"javax\"][class endsWith \"menu\"]",
                selector.toString());
    }

    public void parseSelectorGroupDescendant() throws Throwable {
        SelectorParser parser = new SelectorParser("menu menu-item");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu menu-item", selector.toString());
    }

    public void parseSelectorGroupChild() throws Throwable {
        SelectorParser parser = new SelectorParser("menu>menu-item");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu > menu-item", selector.toString());
    }

    public void parseSelectorGroupAdjacentSibling() throws Throwable {
        SelectorParser parser = new SelectorParser("menu+menu-item");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu + menu-item", selector.toString());
    }

    public void parseSelectorGeneralSibling() throws Throwable {
        SelectorParser parser = new SelectorParser("menu   ~   menu-item");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("menu ~ menu-item", selector.toString());
    }

    public void parseSelectorGroup() throws Throwable {
        SelectorParser parser = new SelectorParser("input[type=\"text\"]:focus");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("input[type = \"text\"]:focus", selector.toString());
    }

    public void parseWhiteSpaceWorksProperly() throws Throwable {
        SelectorParser parser = new SelectorParser("panel *:selected");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("panel *:selected", selector.toString());
    }

    public void parseElementSelector() throws Throwable {
        SelectorParser parser = new SelectorParser("panel ::selected");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("panel *::selected", selector.toString());
    }

    public void parseSelfSelector() throws Throwable {
        SelectorParser parser = new SelectorParser(".::selected");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals(".::selected", selector.toString());
    }

    public void parseNthPseudoClass() throws Throwable {
        SelectorParser parser = new SelectorParser("panel:nth(1)");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals("panel:nth(1)", selector.toString());
    }

    public void parseNthPseudoClassThrowsErrorIfNotLast() throws Throwable {
        SelectorParser parser = new SelectorParser("panel:nth(1):enabled");
        try {
            parser.parse();
            throw new MissingException(ParserException.class);
        } catch (ParserException e) {
        }
    }

    public void parseSelfSelectorWithParams() throws Throwable {
        SelectorParser parser = new SelectorParser(".::call-select('Hello World')");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals(".::call-select(\"Hello World\")", selector.toString());
    }

    public void parseSelfSelectorWithParamsEscapeQuote() throws Throwable {
        SelectorParser parser = new SelectorParser(".::call-select('Hello\\' World')");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals(".::call-select(\"Hello' World\")", selector.toString());
    }

    public void parseSelfSelectorWithParamsEscapeBackslash() throws Throwable {
        SelectorParser parser = new SelectorParser(".::call-select('Hello\\\\ World')");
        Selector selector = parser.parse();
        AssertJUnit.assertNotNull(selector);
        AssertJUnit.assertEquals(".::call-select(\"Hello\\ World\")", selector.toString());
    }

    public void parseSomewhatComplicatedStuff() throws Throwable {
        String value = "Hello\n\\' is a single quote\n\" is a double quote\nand this one has some \\n - new lines\n\nDoes this work?";
        SelectorParser parser = new SelectorParser(".::call-select('" + value + "')");
        parser.parse();
    }
}
