package net.sourceforge.marathon.javafxagent.css;

import net.sourceforge.marathon.javafxagent.css.LexerException;
import net.sourceforge.marathon.javafxagent.css.SelectorLexer;
import net.sourceforge.marathon.javafxagent.css.Token;
import net.sourceforge.marathon.javafxagent.css.TokenType;
import net.sourceforge.marathon.testhelpers.MissingException;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

@Test public class SelectorLexerTest {

    public void nextTokenIdent() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("menu");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_IDENTIFIER, nextToken.getType());
        AssertJUnit.assertEquals("menu", nextToken.getValue());
    }

    public void nextTokenIdentHiphenated() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("scroll-bar");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_TAG, nextToken.getType());
        AssertJUnit.assertEquals("scroll-bar", nextToken.getValue());
    }

    public void nextTokenAttribute() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("menu.name");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_ATTRIBUTE, nextToken.getType());
        AssertJUnit.assertEquals("menu.name", nextToken.getValue());
    }

    public void nextTokenIdentError() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("-menu-error");
        try {
            selectorLexer.nextToken();
            throw new MissingException(LexerException.class);
        } catch (LexerException e) {

        }
    }

    public void nextTokenInteger() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("12342");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_INTEGER, nextToken.getType());
        AssertJUnit.assertEquals("12342", nextToken.getValue());
    }

    public void nextTokenNumber() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("123.42");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_NUMBER, nextToken.getType());
        AssertJUnit.assertEquals("123.42", nextToken.getValue());
    }

    public void nextTokenNumberGetsOnlyOneDot() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("123.42.21");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_NUMBER, nextToken.getType());
        AssertJUnit.assertEquals("123.42", nextToken.getValue());
    }

    public void nextTokenStringWithSingleQuotes() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("'123.42'");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_STRING, nextToken.getType());
        AssertJUnit.assertEquals("123.42", nextToken.getValue());
    }

    public void nextTokenStringWithDoubleQuotes() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("\"123.42\"");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_STRING, nextToken.getType());
        AssertJUnit.assertEquals("123.42", nextToken.getValue());
    }

    public void nextTokenStringWithDoubleQuotesEscaped() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("\"123.42\\n\\\"\"");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_STRING, nextToken.getType());
        AssertJUnit.assertEquals("123.42\n\"", nextToken.getValue());
    }

    public void nextTokenStringWithSingleQuotesErrorNotTerminated() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("'123.42");
        try {
            selectorLexer.nextToken();
            throw new MissingException(LexerException.class);
        } catch (LexerException e) {

        }
    }

    public void nextTokenID() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("#-hello-world");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_ID, nextToken.getType());
        AssertJUnit.assertEquals("-hello-world", nextToken.getValue());
    }

    public void nextTokenIDAsString() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("#'hello world'");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_ID, nextToken.getType());
        AssertJUnit.assertEquals("hello world", nextToken.getValue());
    }

    public void nextTokenPseudoClass() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer(":menu-function");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PSEUDO_CLASS, nextToken.getType());
        AssertJUnit.assertEquals("menu-function", nextToken.getValue());
    }

    public void nextTokenPsuedoClassWithArgs() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer(":menu-function(");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PSEUDO_CLASS_ARGS, nextToken.getType());
        AssertJUnit.assertEquals("menu-function", nextToken.getValue());
    }

    public void nextTokenPseudoElement() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("::menu-function");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PSEUDO_ELEMENT, nextToken.getType());
        AssertJUnit.assertEquals("menu-function", nextToken.getValue());
    }

    public void nextTokenPseudoElementWithArgs() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer("::menu-function(");
        Token nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PSEUDO_ELEMENT_ARGS, nextToken.getType());
        AssertJUnit.assertEquals("menu-function", nextToken.getValue());
    }

    public void nextTokenOperators() throws Throwable {
        SelectorLexer selectorLexer = new SelectorLexer(">+~=^=$=*=");
        Token nextToken;
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_GREATER, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PLUS, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_TILDE, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_EQUALS, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_PREFIXMATCH, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_SUFFIXMATCH, nextToken.getType());
        nextToken = selectorLexer.nextToken();
        AssertJUnit.assertEquals(TokenType.TT_SUBSTRINGMATCH, nextToken.getType());
    }

    public void expects() throws Throwable {
        SelectorLexer lexer = new SelectorLexer("tr:nth-child(odd)");
        Token nextToken;
        nextToken = lexer.expect1(TokenType.TT_IDENTIFIER);
        AssertJUnit.assertEquals(TokenType.TT_IDENTIFIER, nextToken.getType());
        AssertJUnit.assertEquals("tr", nextToken.getValue());
        nextToken = lexer.expect1(TokenType.TT_PSEUDO_CLASS_ARGS);
        AssertJUnit.assertEquals(TokenType.TT_PSEUDO_CLASS_ARGS, nextToken.getType());
        AssertJUnit.assertEquals("nth-child", nextToken.getValue());
        nextToken = lexer.expect1(TokenType.TT_IDENTIFIER);
        AssertJUnit.assertEquals(TokenType.TT_IDENTIFIER, nextToken.getType());
        AssertJUnit.assertEquals("odd", nextToken.getValue());
        nextToken = lexer.expect1r0(TokenType.TT_CLOSE_PAREN);
        AssertJUnit.assertEquals(TokenType.TT_CLOSE_PAREN, nextToken.getType());
    }
}
