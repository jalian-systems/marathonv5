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

import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractLexer implements ILexer {

    public static final Logger LOGGER = Logger.getLogger(AbstractLexer.class.getName());

    protected PushbackReader reader;
    private Token laToken;

    protected boolean ignoreWhitespace = true;

    public AbstractLexer(PushbackReader reader) {
        this.reader = reader;
    }

    final Token nextToken() {
        Token token;
        if (laToken == null) {
            token = getNextToken();
        } else {
            token = laToken;
        }
        laToken = null;
        return token;
    }

    abstract protected Token getNextToken();

    @Override
    public Token expect1(TokenType... types) {
        Token t = expect1r0(types);
        if (t == null) {
            t = nextToken();
            List<TokenType> asList = new ArrayList<TokenType>(Arrays.asList(types));
            throw new ParserException("Expecting one of " + asList + " Got: " + t, null);
        }
        return t;
    }

    private Token findMatchingToken(Token la, TokenType[] types) {
        for (TokenType type : types) {
            if (la.getType() == type) {
                return nextToken();
            }
        }
        return null;
    }

    @Override
    public Token expect1r0(TokenType... types) {
        ignoreWhitespace = !Arrays.asList(types).contains(TokenType.TT_WHITESPACE);
        return findMatchingToken(lookAhead(), types);
    }

    public final Token lookAhead() {
        if (laToken != null) {
            return laToken;
        }
        laToken = getNextToken();
        return laToken;
    }

}
