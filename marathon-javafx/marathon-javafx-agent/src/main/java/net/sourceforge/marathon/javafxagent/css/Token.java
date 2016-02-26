package net.sourceforge.marathon.javafxagent.css;

public class Token {
    private final TokenType type;
    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        this(type, null);
    }

    @Override public String toString() {
        return type.toString() + (value != null ? "(" + value + ")" : "");
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
