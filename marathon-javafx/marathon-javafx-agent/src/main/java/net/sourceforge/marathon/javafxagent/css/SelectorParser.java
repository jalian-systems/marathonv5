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
package net.sourceforge.marathon.javafxagent.css;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SelectorParser {

    public static final Logger LOGGER = Logger.getLogger(SelectorParser.class.getName());

    private AbstractLexer lexer;

    public SelectorParser(String selector) {
        this.lexer = new SelectorLexer(selector);
    }

    public Selector parse() {
        Selector result = readSimpleSelector();
        while (true) {
            Token isEof = lexer.expect1r0(TokenType.TT_EOF);
            if (isEof != null) {
                return result;
            }
            Token combinator = lexer.expect1r0(TokenType.TT_GREATER, TokenType.TT_PLUS, TokenType.TT_TILDE);
            if (combinator == null) {
                result = new DescendentSelector(result, readSimpleSelector());
            } else if (combinator.getType() == TokenType.TT_GREATER) {
                result = new ChildSelector(result, readSimpleSelector());
            } else if (combinator.getType() == TokenType.TT_PLUS) {
                result = new AdjacentSiblingSelector(result, readSimpleSelector());
            } else {
                result = new GeneralSiblingSelector(result, readSimpleSelector());
            }
        }
    }

    private SimpleSelector readSimpleSelector() {
        Token tag = lexer.expect1r0(TokenType.TT_IDENTIFIER, TokenType.TT_TAG, TokenType.TT_UNIVERSAL_SELECTOR,
                TokenType.TT_SELF_SELECTOR);
        SimpleSelector selector = new SimpleSelector(tag == null ? "*" : tag.getValue());
        Token t = lexer.expect1r0(TokenType.TT_ID, TokenType.TT_PSEUDO_CLASS, TokenType.TT_PSEUDO_CLASS_ARGS,
                TokenType.TT_PSEUDO_ELEMENT, TokenType.TT_PSEUDO_ELEMENT_ARGS, TokenType.TT_OPEN_BR, TokenType.TT_WHITESPACE);
        if (tag == null && t == null) {
            lexer.expect1(TokenType.TT_ID, TokenType.TT_PSEUDO_CLASS, TokenType.TT_PSEUDO_CLASS_ARGS, TokenType.TT_PSEUDO_ELEMENT,
                    TokenType.TT_PSEUDO_ELEMENT_ARGS, TokenType.TT_OPEN_BR, TokenType.TT_WHITESPACE);
            return null;
        }

        while (t != null) {
            if (t.getType() == TokenType.TT_WHITESPACE) {
                return selector;
            } else if (t.getType() == TokenType.TT_ID) {
                selector.addFilter(new IdFilter(t.getValue()));
            } else if (t.getType() == TokenType.TT_PSEUDO_CLASS) {
                selector.addFilter(new PseudoClassFilter(t.getValue()));
            } else if (t.getType() == TokenType.TT_PSEUDO_CLASS_ARGS) {
                Argument[] args = collectArguments();
                selector.addFilter(new PseudoClassFilter(t.getValue(), args));
            } else if (t.getType() == TokenType.TT_PSEUDO_ELEMENT) {
                selector.addFilter(new PseudoElementFilter(t.getValue()));
            } else if (t.getType() == TokenType.TT_PSEUDO_ELEMENT_ARGS) {
                Argument[] args = collectArguments();
                selector.addFilter(new PseudoElementFilter(t.getValue(), args));
            } else if (t.getType() == TokenType.TT_OPEN_BR) {
                t = lexer.expect1(TokenType.TT_IDENTIFIER, TokenType.TT_ATTRIBUTE);
                Token equals = lexer.expect1r0(TokenType.TT_EQUALS, TokenType.TT_PREFIXMATCH, TokenType.TT_SUBSTRINGMATCH,
                        TokenType.TT_SUFFIXMATCH);
                Argument arg = null;
                if (equals != null) {
                    arg = collectArgument();
                }
                selector.addFilter(new AttributeFilter(t.getValue(), arg, equals != null ? equals.getValue() : null));
                lexer.expect1(TokenType.TT_CLOSE_BR);
            }
            t = lexer.expect1r0(TokenType.TT_ID, TokenType.TT_PSEUDO_CLASS, TokenType.TT_PSEUDO_CLASS_ARGS,
                    TokenType.TT_PSEUDO_ELEMENT, TokenType.TT_PSEUDO_ELEMENT_ARGS, TokenType.TT_OPEN_BR, TokenType.TT_WHITESPACE);
        }
        return selector;
    }

    private Argument[] collectArguments() {
        List<Argument> args = new ArrayList<Argument>();
        while (true) {
            args.add(collectArgument());
            Token t = lexer.expect1(TokenType.TT_COMMA, TokenType.TT_CLOSE_PAREN);
            if (t.getType() == TokenType.TT_CLOSE_PAREN) {
                break;
            }
        }
        return args.toArray(new Argument[args.size()]);
    }

    public Argument collectArgument() {
        Token t = lexer.expect1(TokenType.TT_BOOLEAN, TokenType.TT_INTEGER, TokenType.TT_STRING, TokenType.TT_NUMBER);
        Argument arg;
        if (t.getType() == TokenType.TT_BOOLEAN) {
            arg = new Argument(Boolean.valueOf(t.getValue()));
        } else if (t.getType() == TokenType.TT_INTEGER) {
            arg = new Argument(Integer.parseInt(t.getValue()));
        } else if (t.getType() == TokenType.TT_NUMBER) {
            arg = new Argument(Double.parseDouble(t.getValue()));
        } else {
            arg = new Argument(t.getValue());
        }
        return arg;
    }
}
