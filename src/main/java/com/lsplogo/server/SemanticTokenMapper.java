package com.lsplogo.server;

import com.lsplogo.lexer.Token;
import com.lsplogo.lexer.TokenType;

import java.util.EnumSet;


public class SemanticTokenMapper {

    public static final String KEYWORD = "keyword";
    public static final String VARIABLE = "variable";
    public static final String STRING = "string";
    public static final String NUMBER = "number";
    public static final String FUNCTION = "function";
    public static final String OPERATOR = "operator";
    public static final String COMMENT = "comment";

    private static final EnumSet<TokenType> KEYWORD_TYPES = EnumSet.of(
        TokenType.TO,
        TokenType.DEFINE,
        TokenType.DEF,
        TokenType.END,
        TokenType.REPEAT,
        TokenType.FOR,
        TokenType.IF,
        TokenType.IFELSE,
        TokenType.TEST,
        TokenType.IFTRUE,
        TokenType.IFFALSE,
        TokenType.WAIT,
        TokenType.BY,
        TokenType.DOTIMES,
        TokenType.DOWHILE,
        TokenType.WHILE,
        TokenType.DOUNTIL,
        TokenType.UNTIL,
        TokenType.MAKE,
        TokenType.NAME,
        TokenType.LOCALMAKE,
        TokenType.THING,
        TokenType.FORWARD,
        TokenType.BACKWARD,
        TokenType.LEFT,
        TokenType.RIGHT,
        TokenType.HOME,
        TokenType.SETX,
        TokenType.SETY,
        TokenType.SETXY,
        TokenType.SETH,
        TokenType.ARC,
        TokenType.ELLIPSE,
        TokenType.SHOWTURTLE,
        TokenType.HIDETURTLE,
        TokenType.PENUP,
        TokenType.PENDOWN,
        TokenType.SETCOLOR,
        TokenType.SETWIDTH,
        TokenType.CHANGESHAPE,
        TokenType.CLEAN,
        TokenType.CLEARSCREEN,
        TokenType.FILL,
        TokenType.FILLED,
        TokenType.LABEL,
        TokenType.SETLH,
        TokenType.WRAP,
        TokenType.WINDOW,
        TokenType.FENCE,
        TokenType.POS,
        TokenType.XCOR,
        TokenType.YCOR,
        TokenType.HEADING,
        TokenType.TOWARDS,
        TokenType.PENDOWNP,
        TokenType.PENCOLOR,
        TokenType.PENSIZE,
        TokenType.SHOWNP,
        TokenType.LABELSIZE,
        TokenType.LIST,
        TokenType.FIRST,
        TokenType.BUTFIRST,
        TokenType.LAST,
        TokenType.BUTLAST,
        TokenType.ITEM,
        TokenType.PICK,
        TokenType.SUM,
        TokenType.MINUS,
        TokenType.RANDOM,
        TokenType.MODULO,
        TokenType.POWER,
        TokenType.REPCOUNT,
        TokenType.READWORD,
        TokenType.READLIST
    );

    private static final EnumSet<TokenType> OPERATOR_TYPES = EnumSet.of(
        TokenType.PLUS,
        TokenType.MINUS_OP,
        TokenType.ASTERISK,
        TokenType.SLASH,
        TokenType.EQUALS,
        TokenType.LESS,
        TokenType.GREATER
    );

    public static String mapTokenType(TokenType type) {
        if (type == TokenType.NUMBER) {
            return NUMBER;
        }
        if (type == TokenType.STRING) {
            return STRING;
        }
        if (type == TokenType.VARIABLE) {
            return VARIABLE;
        }
        if (type == TokenType.IDENTIFIER) {
            return FUNCTION;
        }
        if (type == TokenType.COMMENT) {
            return COMMENT;
        }
        if (OPERATOR_TYPES.contains(type)) {
            return OPERATOR;
        }
        if (KEYWORD_TYPES.contains(type)) {
            return KEYWORD;
        }
        return null;
    }

    public static boolean shouldIncludeToken(Token token) {
        TokenType type = token.getType();
        if (type == TokenType.NEWLINE ||
            type == TokenType.EOF ||
            type == TokenType.LBRACKET ||
            type == TokenType.RBRACKET ||
            type == TokenType.LPAREN ||
            type == TokenType.RPAREN ||
            type == TokenType.ERROR) {
            return false;
        }
        return mapTokenType(type) != null;
    }
}
