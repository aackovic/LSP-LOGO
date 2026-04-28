package com.lsplogo.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Lexer {
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("forward", TokenType.FORWARD);
        keywords.put("fd", TokenType.FORWARD);
        keywords.put("backward", TokenType.BACKWARD);
        keywords.put("back", TokenType.BACKWARD);
        keywords.put("bk", TokenType.BACKWARD);
        keywords.put("left", TokenType.LEFT);
        keywords.put("lt", TokenType.LEFT);
        keywords.put("right", TokenType.RIGHT);
        keywords.put("rt", TokenType.RIGHT);
        keywords.put("home", TokenType.HOME);
        keywords.put("setx",  TokenType.SETX);
        keywords.put("sety", TokenType.SETY);
        keywords.put("setxy",  TokenType.SETXY);
        keywords.put("setheading", TokenType.SETH);
        keywords.put("seth", TokenType.SETH);
        keywords.put("arc",  TokenType.ARC);
        keywords.put("ellipse", TokenType.ELLIPSE);

        keywords.put("pos", TokenType.POS);
        keywords.put("xcor", TokenType.XCOR);
        keywords.put("ycor", TokenType.YCOR);
        keywords.put("heading", TokenType.HEADING);
        keywords.put("towards", TokenType.TOWARDS);

        keywords.put("showturtle", TokenType.SHOWTURTLE);
        keywords.put("st", TokenType.SHOWTURTLE);
        keywords.put("hideturtle", TokenType.HIDETURTLE);
        keywords.put("ht", TokenType.HIDETURTLE);
        keywords.put("clean", TokenType.CLEAN);
        keywords.put("clearscreen", TokenType.CLEARSCREEN);
        keywords.put("cs", TokenType.CLEARSCREEN);
        keywords.put("fill", TokenType.FILL);
        keywords.put("filled", TokenType.FILLED);
        keywords.put("label", TokenType.LABEL);
        keywords.put("setlabelheight", TokenType.SETLH);
        keywords.put("setlh", TokenType.SETLH);
        keywords.put("wrap", TokenType.WRAP);
        keywords.put("window", TokenType.WINDOW);
        keywords.put("fence", TokenType.FENCE);

        keywords.put("shownp", TokenType.SHOWNP);
        keywords.put("labelsize", TokenType.LABELSIZE);

        keywords.put("penup", TokenType.PENUP);
        keywords.put("pu", TokenType.PENUP);
        keywords.put("pendown", TokenType.PENDOWN);
        keywords.put("pd", TokenType.PENDOWN);
        keywords.put("setcolor", TokenType.SETCOLOR);
        keywords.put("setwidth", TokenType.SETWIDTH);
        keywords.put("changeshape", TokenType.CHANGESHAPE);

        keywords.put("pendownp", TokenType.PENDOWNP);
        keywords.put("pencolor", TokenType.PENCOLOR);
        keywords.put("pensize", TokenType.PENSIZE);

        keywords.put("to", TokenType.TO);
        keywords.put("define", TokenType.DEFINE);
        keywords.put("def", TokenType.DEF);
        keywords.put("end", TokenType.END);

        keywords.put("make", TokenType.MAKE);
        keywords.put("name", TokenType.NAME);
        keywords.put("localmake", TokenType.LOCALMAKE);
        keywords.put("thing", TokenType.THING);

        keywords.put("repeat", TokenType.REPEAT);
        keywords.put("for", TokenType.FOR);
        keywords.put("repcount", TokenType.REPCOUNT);
        keywords.put("if", TokenType.IF);
        keywords.put("ifelse", TokenType.IFELSE);
        keywords.put("test", TokenType.TEST);
        keywords.put("iftrue", TokenType.IFTRUE);
        keywords.put("iffalse", TokenType.IFFALSE);
        keywords.put("wait", TokenType.WAIT);
        keywords.put("by", TokenType.BY);
        keywords.put("dotimes", TokenType.DOTIMES);
        keywords.put("dowhile", TokenType.DOWHILE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("dountil", TokenType.DOUNTIL);
        keywords.put("until", TokenType.UNTIL);

        keywords.put("list", TokenType.LIST);
        keywords.put("first", TokenType.FIRST);
        keywords.put("butfirst", TokenType.BUTFIRST);
        keywords.put("last", TokenType.LAST);
        keywords.put("butlast", TokenType.BUTLAST);
        keywords.put("item", TokenType.ITEM);
        keywords.put("pick", TokenType.PICK);

        keywords.put("sum", TokenType.SUM);
        keywords.put("minus", TokenType.MINUS);
        keywords.put("random", TokenType.RANDOM);
        keywords.put("modulo", TokenType.MODULO);
        keywords.put("power", TokenType.POWER);

        keywords.put("readword", TokenType.READWORD);
        keywords.put("readlist", TokenType.READLIST);
    }

    private final String sourceCode;
    private int pos = 0;
    private int line = 0;
    private int column = 0;

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /*
    tokenizer scans for tokens and returns list of tokens in source code
    whitespaces and tabs are skipped
    simple tokens (brackets, operators) are handled independently
    ; handles comments
    : handles variables
    " handles strings
    the rest are either numbers of keywords of LOGO language
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();

        while (!endOfInput()) {
            int currLine = line;
            int currColumn = column;
            char current = advance();

            switch (current) {
                case ' ':
                case '\t':
                case '\r':
                    break;
                case '\n':
                    tokens.add(new Token(TokenType.NEWLINE, "\\n", currLine, currColumn));
                    line++;
                    column = 0;
                    break;
                case '[':
                    tokens.add(new Token(TokenType.LBRACKET, "[", currLine, currColumn));
                    break;
                case ']':
                    tokens.add(new Token(TokenType.RBRACKET, "]", currLine, currColumn));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(", currLine, currColumn));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")", currLine, currColumn));
                    break;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+", currLine, currColumn));
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINUS_OP, "-", currLine, currColumn));
                    break;
                case '*':
                    tokens.add(new Token(TokenType.ASTERISK, "*", currLine, currColumn));
                    break;
                case '/':
                    tokens.add(new Token(TokenType.SLASH, "/", currLine, currColumn));
                    break;
                case '=':
                    tokens.add(new Token(TokenType.EQUALS, "=", currLine, currColumn));
                    break;
                case '<':
                    tokens.add(new Token(TokenType.LESS, "<", currLine, currColumn));
                    break;
                case '>':
                    tokens.add(new Token(TokenType.GREATER, ">", currLine, currColumn));
                    break;
                case ';':
                    readComment(tokens, currLine, currColumn);
                    break;
                case ':':
                    readVariable(tokens, currLine, currColumn);
                    break;
                case '"':
                    readString(tokens, currLine, currColumn);
                    break;
                default:
                    if (Character.isDigit(current)) {
                        readNumber(tokens, current, currLine, currColumn);
                    } else if (isWordStart(current)) {
                        readWordOrKeyword(tokens, current, currLine, currColumn);
                    } else {
                        tokens.add(new Token(TokenType.ERROR, String.valueOf(current), currLine, currColumn));
                    }
                    break;
            }
        }

        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    private boolean endOfInput() {
        return pos >= sourceCode.length();
    }

    private char advance() {
        char current = sourceCode.charAt(pos++);
        column++;
        return current;
    }

    private char peek() {
        if (endOfInput()) {
            return '\0';
        }
        return sourceCode.charAt(pos);
    }

    private boolean isWordStart(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isWordPart(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_';
    }

    private void readNumber(List<Token> tokens, char startDigit, int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();
        value.append(startDigit);

        while (Character.isDigit(peek())) {
            value.append(advance());
        }

        if (peek() == '.') {
            value.append(advance());
            while (Character.isDigit(peek())) {
                value.append(advance());
            }
        }

        tokens.add(new Token(TokenType.NUMBER, value.toString(), startLine, startColumn));
    }

    private void readWordOrKeyword(List<Token> tokens, char firstChar, int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();
        value.append(firstChar);

        while (isWordPart(peek())) {
            value.append(advance());
        }

        String normalizedText = value.toString().toLowerCase(Locale.ROOT);
        TokenType type = keywords.get(normalizedText);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        tokens.add(new Token(type, normalizedText, startLine, startColumn));
    }

    private void readVariable(List<Token> tokens, int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();

        while (isWordPart(peek())) {
            value.append(advance());
        }

        tokens.add(new Token(TokenType.VARIABLE, value.toString().toLowerCase(Locale.ROOT), startLine, startColumn, value.length() + 1));
    }

    private void readString(List<Token> tokens, int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();

        while (!endOfInput() && peek() != ' ' && peek() != '\t' && peek() != '\n') {
            value.append(advance());
        }

        tokens.add(new Token(TokenType.STRING, value.toString().trim(), startLine, startColumn, value.length() + 1));
    }

    private void readComment(List<Token> tokens, int startLine, int startColumn) {
        StringBuilder value = new StringBuilder();
        
        while (!endOfInput() && peek() != '\n') {
            value.append(advance());
        }

        tokens.add(new Token(TokenType.COMMENT, value.toString(), startLine, startColumn, value.length() + 1));
    }
}
