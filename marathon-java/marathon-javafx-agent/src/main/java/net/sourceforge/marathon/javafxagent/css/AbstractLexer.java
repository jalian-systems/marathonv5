package net.sourceforge.marathon.javafxagent.css;

import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractLexer implements ILexer {

    protected PushbackReader reader;
    private Token laToken;

    protected boolean ignoreWhitespace = true;

    public AbstractLexer(PushbackReader reader) {
        this.reader = reader;
    }

    final Token nextToken() {
        Token token;
        if (laToken == null)
            token = getNextToken();
        else
            token = laToken;
        laToken = null;
        return token;
    }

    abstract protected Token getNextToken();

    @Override public Token expect1(TokenType... types) {
        Token t = expect1r0(types);
        if (t == null) {
            t = nextToken();
            List<TokenType> asList = new ArrayList<TokenType>(Arrays.asList(types));
            throw new ParserException("Expecting one of " + asList + " Got: " + t, null);
        }
        return t;
    }

    private Token findMatchingToken(Token la, TokenType[] types) {
        for (int i = 0; i < types.length; i++) {
            if (la.getType() == types[i]) {
                return nextToken();
            }
        }
        return null;
    }

    @Override public Token expect1r0(TokenType... types) {
        ignoreWhitespace = !Arrays.asList(types).contains(TokenType.TT_WHITESPACE);
        return findMatchingToken(lookAhead(), types);
    }

    public final Token lookAhead() {
        if (laToken != null)
            return laToken;
        laToken = getNextToken();
        return laToken;
    }

}
