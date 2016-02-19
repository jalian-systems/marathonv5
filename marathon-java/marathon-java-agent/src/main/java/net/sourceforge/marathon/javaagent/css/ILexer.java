package net.sourceforge.marathon.javaagent.css;

public interface ILexer {

    public Token expect1(TokenType... types);

    public Token expect1r0(TokenType... types);

}
