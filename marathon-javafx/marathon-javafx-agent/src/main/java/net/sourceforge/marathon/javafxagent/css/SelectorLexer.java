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

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

public class SelectorLexer extends AbstractLexer {

    public SelectorLexer(String selector) {
        super(new PushbackReader(new StringReader(selector)));
    }

    @Override protected Token getNextToken() {

        int c;
        try {
            while ((c = reader.read()) != -1) {
                int la = reader.read();
                if (la != -1) {
                    reader.unread(la);
                }
                if (Character.isWhitespace(c)) {
                    while (Character.isWhitespace(c)) {
                        c = reader.read();
                    }
                    if (c != -1) {
                        reader.unread(c);
                    }
                    if (ignoreWhitespace) {
                        continue;
                    }
                    return new Token(TokenType.TT_WHITESPACE);
                } else if (c == '"' || c == '\'') {
                    return readString(c);
                } else if (c == ',') {
                    return new Token(TokenType.TT_COMMA);
                } else if (c == '[') {
                    return new Token(TokenType.TT_OPEN_BR);
                } else if (c == ']') {
                    return new Token(TokenType.TT_CLOSE_BR);
                } else if (c == '.') {
                    return new Token(TokenType.TT_SELF_SELECTOR, ".");
                } else if (c == '(') {
                    return new Token(TokenType.TT_OPEN_PAREN);
                } else if (c == ')') {
                    return new Token(TokenType.TT_CLOSE_PAREN);
                } else if (c == '>') {
                    return new Token(TokenType.TT_GREATER);
                } else if (c == '+') {
                    return new Token(TokenType.TT_PLUS);
                } else if (c == '~') {
                    return new Token(TokenType.TT_TILDE);
                } else if (c == '=') {
                    return new Token(TokenType.TT_EQUALS, "=");
                } else if (c == '^' && la == '=') {
                    reader.read();
                    return new Token(TokenType.TT_PREFIXMATCH, "startsWith");
                } else if (c == '$' && la == '=') {
                    reader.read();
                    return new Token(TokenType.TT_SUFFIXMATCH, "endsWith");
                } else if (c == '*') {
                    if (la == '=') {
                        reader.read();
                        return new Token(TokenType.TT_SUBSTRINGMATCH, "contains");
                    }
                    return new Token(TokenType.TT_UNIVERSAL_SELECTOR, "*");
                } else if (c == '#') {
                    return readId();
                } else if (c == ':') {
                    if (la == ':') {
                        reader.read();
                    }
                    return readPseudoItem(la == ':' ? TokenType.TT_PSEUDO_ELEMENT : TokenType.TT_PSEUDO_CLASS);
                } else if (Character.isDigit(c)) {
                    return readInteger(c);
                } else if (Character.isJavaIdentifierStart(c)) {
                    return readIdentifier(c);
                } else {
                    throw new LexerException("While reading template start token -- unexpected character '" + (char) c + "'", null);
                }
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        return new Token(TokenType.TT_EOF);
    }

    private Token readId() {
        StringBuffer sb = new StringBuffer();
        int c;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '"' || c == '\'') {
                    Token t = readString(c);
                    return new Token(TokenType.TT_ID, t.getValue());
                }
                if (Character.isJavaIdentifierPart(c) || c == '-') {
                    sb.append((char) c);
                } else {
                    break;
                }
            }
            if (c != -1) {
                reader.unread(c);
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        if (sb.length() == 0) {
            throw new LexerException("While reading hashed id -- unexpected character '" + (char) c + "'", null);
        }
        return new Token(TokenType.TT_ID, sb.toString());
    }

    private Token readPseudoItem(TokenType tt) {
        StringBuffer sb = new StringBuffer();
        int c = -1;
        boolean first = false;
        TokenType type = tt;
        try {
            while ((c = reader.read()) != -1) {
                if (first && Character.isJavaIdentifierPart(c)) {
                    first = false;
                    sb.append((char) c);
                } else if ((c == '-' || Character.isJavaIdentifierPart(c)) && c != '$') {
                    sb.append((char) c);
                } else {
                    break;
                }
            }
            if (c == '(') {
                if (tt == TokenType.TT_PSEUDO_CLASS) {
                    type = TokenType.TT_PSEUDO_CLASS_ARGS;
                } else {
                    type = TokenType.TT_PSEUDO_ELEMENT_ARGS;
                }
            } else if (c != -1) {
                reader.unread(c);
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        if (sb.length() == 0) {
            throw new LexerException("While reading pseudo element/class -- unexpected character '" + (char) c + "'", null);
        }
        return new Token(type, sb.toString());
    }

    protected Token readIdentifier(int initial) {
        StringBuffer sb = new StringBuffer();
        sb.append((char) initial);
        int c;
        boolean isAttr = false;
        boolean isTag = false;
        try {
            while ((c = reader.read()) != -1) {
                if ((c == '-' || c == '.' || Character.isJavaIdentifierPart(c)) && c != '$') {
                    if (c == '.') {
                        isAttr = true;
                    }
                    if (c == '-') {
                        isTag = true;
                    }
                    sb.append((char) c);
                } else {
                    break;
                }
            }
            if (c != -1) {
                reader.unread(c);
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        if (sb.length() == 0) {
            throw new LexerException("While reading identifier -- unexpected character '" + (char) c + "'", null);
        }
        String value = sb.toString();
        if (value.equals("true")) {
            return new Token(TokenType.TT_BOOLEAN, value);
        } else if (value.equals("false")) {
            return new Token(TokenType.TT_BOOLEAN, value);
        }
        if (isAttr && isTag) {
            throw new LexerException("Unable to distinguish between tag and attribute.", null);
        }
        TokenType type = TokenType.TT_IDENTIFIER;
        if (isAttr) {
            type = TokenType.TT_ATTRIBUTE;
        } else if (isTag) {
            type = TokenType.TT_TAG;
        }
        return new Token(type, value);
    }

    protected Token readInteger(int initial) {
        StringBuffer sb = new StringBuffer();
        sb.append((char) initial);
        int c;
        boolean number = false;
        try {
            while ((c = reader.read()) != -1) {
                if (Character.isDigit(c) || !number && c == '.') {
                    if (c == '.') {
                        number = true;
                    }
                    sb.append((char) c);
                } else {
                    break;
                }
            }
            if (c != -1) {
                reader.unread(c);
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        return new Token(number ? TokenType.TT_NUMBER : TokenType.TT_INTEGER, sb.toString());
    }

    protected Token readString(int sep) {
        StringBuffer sb = new StringBuffer();
        int c;
        boolean escape = false;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '\\' && !escape) {
                    escape = true;
                    continue;
                }
                if (escape) {
                    if (c == 'n') {
                        sb.append('\n');
                    } else {
                        sb.append((char) c);
                    }
                    escape = false;
                    continue;
                }
                if (c == sep) {
                    return new Token(TokenType.TT_STRING, sb.toString());
                }
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new LexerException("IOError: " + e.getMessage(), e);
        }
        throw new LexerException("While reading string - unexpected EOF", null);
    }

}
