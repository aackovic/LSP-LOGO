package com.lsplogo.lexer;

public class Token {
    private final TokenType type;
    private final String value;
    private final int  line;
    private final int column;
    private final int length;

    public Token(TokenType type, String value, int line, int column) {
        this(type, value, line, column, value.length());
    }

    public Token(TokenType type, String value, int line, int column, int length) {
        this.type = type;
        this.value = value;
        this.line = line;
        this.column = column;
        this.length = length;
    }

    @Override
    public String toString() {
        return type.toString() + " " + value + " at line " + line + " and column " + column;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getLength() {
        return length;
    }

}
