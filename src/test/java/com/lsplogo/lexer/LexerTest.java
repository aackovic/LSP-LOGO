package com.lsplogo.lexer;

import junit.framework.TestCase;
import java.util.List;

public class LexerTest extends TestCase {

    private void assertTokenType(TokenType expected, TokenType actual, String message) {
        assertEquals(message, expected, actual);
    }

    private void assertTokenValue(String expected, String actual, String message) {
        assertEquals(message, expected, actual);
    }

    public void testEmptyInput() {
        Lexer lexer = new Lexer("");
        List<Token> tokens = lexer.tokenize();
        assertNotNull("tokenize should not return null", tokens);
        assertEquals("empty input should only have EOF", 1, tokens.size());
        assertTokenType(TokenType.EOF, tokens.get(0).getType(), "first token should be EOF");
    }

    public void testKeywordForward() {
        Lexer lexer = new Lexer("forward");
        List<Token> tokens = lexer.tokenize();
        assertEquals("forward + EOF", 2, tokens.size());
        assertTokenType(TokenType.FORWARD, tokens.get(0).getType(), "forward should be FORWARD");
        assertTokenValue("forward", tokens.get(0).getValue(), "token value should match");
        assertTokenType(TokenType.EOF, tokens.get(1).getType(), "second token should be EOF");
    }

    public void testKeywordAlias() {
        Lexer lexer = new Lexer("fd bk lt rt");
        List<Token> tokens = lexer.tokenize();
        assertEquals("fd bk lt rt + EOF", 5, tokens.size());
        assertTokenType(TokenType.FORWARD, tokens.get(0).getType(), "fd is alias for forward");
        assertTokenType(TokenType.BACKWARD, tokens.get(1).getType(), "bk is alias for backward");
        assertTokenType(TokenType.LEFT, tokens.get(2).getType(), "lt is alias for left");
        assertTokenType(TokenType.RIGHT, tokens.get(3).getType(), "rt is alias for right");
    }

    public void testCaseInsensitiveKeywords() {
        Lexer lexer = new Lexer("FORWARD Forward fd FD");
        List<Token> tokens = lexer.tokenize();
        assertEquals("four forwards + EOF", 5, tokens.size());
        for (int i = 0; i < 4; i++) {
            assertTokenType(TokenType.FORWARD, tokens.get(i).getType(), "keyword at index " + i + " should be FORWARD");
        }
    }

    public void testIdentifier() {
        Lexer lexer = new Lexer("myProcedure var_123");
        List<Token> tokens = lexer.tokenize();
        assertEquals("two identifiers + EOF", 3, tokens.size());
        assertTokenType(TokenType.IDENTIFIER, tokens.get(0).getType(), "unknown word should be IDENTIFIER");
        assertTokenValue("myprocedure", tokens.get(0).getValue(), "identifier value normalized to lowercase");
        assertTokenType(TokenType.IDENTIFIER, tokens.get(1).getType(), "underscore identifier should work");
        assertTokenValue("var_123", tokens.get(1).getValue(), "underscore identifier value normalized to lowercase");
    }

    public void testVariable() {
        Lexer lexer = new Lexer(":x :var :_var123");
        List<Token> tokens = lexer.tokenize();
        assertEquals("three variables + EOF", 4, tokens.size());
        assertTokenType(TokenType.VARIABLE, tokens.get(0).getType(), "colon prefix marks variable");
        assertTokenValue("x", tokens.get(0).getValue(), "variable name should match");
        assertTokenType(TokenType.VARIABLE, tokens.get(1).getType(), "variable multi-char");
        assertTokenValue("var", tokens.get(1).getValue(), "multi-char variable value normalized to lowercase");
        assertTokenType(TokenType.VARIABLE, tokens.get(2).getType(), "variable with underscore");
    }

    public void testNumber() {
        Lexer lexer = new Lexer("50 0 100");
        List<Token> tokens = lexer.tokenize();
        assertEquals("three numbers + EOF", 4, tokens.size());
        assertTokenType(TokenType.NUMBER, tokens.get(0).getType(), "integer should be NUMBER");
        assertTokenValue("50", tokens.get(0).getValue(), "number value should match");
        assertTokenType(TokenType.NUMBER, tokens.get(1).getType(), "zero should be NUMBER");
        assertTokenValue("0", tokens.get(1).getValue(), "number value should match");
        assertTokenType(TokenType.NUMBER, tokens.get(2).getType(), "hundred should be NUMBER");
        assertTokenValue("100", tokens.get(2).getValue(), "number value should match");
    }

    public void testDecimalNumber() {
        Lexer lexer = new Lexer("3.14 0.5 10.0");
        List<Token> tokens = lexer.tokenize();
        assertEquals("three decimals + EOF", 4, tokens.size());
        assertTokenType(TokenType.NUMBER, tokens.get(0).getType(), "decimal should be NUMBER");
        assertTokenValue("3.14", tokens.get(0).getValue(), "decimal value should match");
        assertTokenValue("0.5", tokens.get(1).getValue(), "decimal value with first zero should match");
        assertTokenValue("10.0", tokens.get(2).getValue(), "decimal value with trailing zero should match");
    }

    public void testString() {
        Lexer lexer = new Lexer("\"hello \"turtle_graphics");
        List<Token> tokens = lexer.tokenize();
        assertEquals("two strings + EOF", 3, tokens.size());
        assertTokenType(TokenType.STRING, tokens.get(0).getType(), "quoted text should be STRING");
        assertTokenValue("hello", tokens.get(0).getValue(), "string should match");
        assertTokenType(TokenType.STRING, tokens.get(1).getType(), "multi-word string");
        assertTokenValue("turtle_graphics", tokens.get(1).getValue(), "string value should match");
    }

    public void testOperators() {
        Lexer lexer = new Lexer("+ - * / = < >");
        List<Token> tokens = lexer.tokenize();
        assertEquals("seven operators + EOF", 8, tokens.size());
        assertTokenType(TokenType.PLUS, tokens.get(0).getType(), "plus operator");
        assertTokenType(TokenType.MINUS_OP, tokens.get(1).getType(), "minus operator");
        assertTokenType(TokenType.ASTERISK, tokens.get(2).getType(), "star operator");
        assertTokenType(TokenType.SLASH, tokens.get(3).getType(), "slash operator");
        assertTokenType(TokenType.EQUALS, tokens.get(4).getType(), "equals operator");
        assertTokenType(TokenType.LESS, tokens.get(5).getType(), "less operator");
        assertTokenType(TokenType.GREATER, tokens.get(6).getType(), "greater operator");
    }

    public void testBrackets() {
        Lexer lexer = new Lexer("[ ] ( )");
        List<Token> tokens = lexer.tokenize();
        assertEquals("four brackets + EOF", 5, tokens.size());
        assertTokenType(TokenType.LBRACKET, tokens.get(0).getType(), "left bracket");
        assertTokenType(TokenType.RBRACKET, tokens.get(1).getType(), "right bracket");
        assertTokenType(TokenType.LPAREN, tokens.get(2).getType(), "left parenthesis");
        assertTokenType(TokenType.RPAREN, tokens.get(3).getType(), "right parenthesis");
    }

    public void testNewline() {
        Lexer lexer = new Lexer("forward\nbackward");
        List<Token> tokens = lexer.tokenize();
        assertEquals("forward newline backward + EOF", 4, tokens.size());
        assertTokenType(TokenType.FORWARD, tokens.get(0).getType(), "first keyword");
        assertTokenType(TokenType.NEWLINE, tokens.get(1).getType(), "newline token");
        assertTokenType(TokenType.BACKWARD, tokens.get(2).getType(), "second keyword");
        assertTokenType(TokenType.EOF, tokens.get(3).getType(), "EOF at end");
    }

    public void testWhitespaceSkipped() {
        Lexer lexer = new Lexer("forward  \t  backward");
        List<Token> tokens = lexer.tokenize();
        assertEquals("forward backward + EOF (spaces/tabs skipped)", 3, tokens.size());
        assertTokenType(TokenType.FORWARD, tokens.get(0).getType(), "first keyword");
        assertTokenType(TokenType.BACKWARD, tokens.get(1).getType(), "second keyword");
    }

    public void testPositionTracking() {
        Lexer lexer = new Lexer("forward 42");
        List<Token> tokens = lexer.tokenize();
        assertEquals("forward number + EOF", 3, tokens.size());
        
        Token firstToken = tokens.get(0);
        assertEquals("first token line", 0, firstToken.getLine());
        assertEquals("first token column", 0, firstToken.getColumn());
        
        Token secondToken = tokens.get(1);
        assertEquals("second token line (same line)", 0, secondToken.getLine());
        assertTrue("second token column after first token", secondToken.getColumn() > 0);
    }

    public void testLineNumberIncrement() {
        Lexer lexer = new Lexer("forward\nbackward\nhome");
        List<Token> tokens = lexer.tokenize();
        
        Token forward = tokens.get(0);
        assertEquals("forward at line 0", 0, forward.getLine());
        
        Token newline1 = tokens.get(1);
        assertEquals("first newline at line 0", 0, newline1.getLine());
        
        Token backward = tokens.get(2);
        assertEquals("backward at line 1", 1, backward.getLine());
        
        Token newline2 = tokens.get(3);
        assertEquals("second newline at line 1", 1, newline2.getLine());
        
        Token home = tokens.get(4);
        assertEquals("home at line 2", 2, home.getLine());
    }

    public void testUnknownCharacter() {
        Lexer lexer = new Lexer("@");
        List<Token> tokens = lexer.tokenize();
        assertEquals("unknown + EOF", 2, tokens.size());
        assertTokenType(TokenType.ERROR, tokens.get(0).getType(), "@ should be UNKNOWN");
        assertTokenValue("@", tokens.get(0).getValue(), "unknown value preserved");
    }

    public void testProcedureDefinition() {
        Lexer lexer = new Lexer("to square :size\nforward :size\nend");
        List<Token> tokens = lexer.tokenize();
        
        assertTokenType(TokenType.TO, tokens.get(0).getType(), "to keyword");
        assertTokenType(TokenType.IDENTIFIER, tokens.get(1).getType(), "procedure name");
        assertTokenValue("square", tokens.get(1).getValue(), "procedure name value");
        
        assertTokenType(TokenType.VARIABLE, tokens.get(2).getType(), "parameter :size");
        
        assertTokenType(TokenType.NEWLINE, tokens.get(3).getType(), "newline after declaration");
        
        assertTokenType(TokenType.FORWARD, tokens.get(4).getType(), "forward in body");
        assertTokenType(TokenType.VARIABLE, tokens.get(5).getType(), "variable in body");
        
        assertTokenType(TokenType.NEWLINE, tokens.get(6).getType(), "newline after body");
        
        assertTokenType(TokenType.END, tokens.get(7).getType(), "end keyword");
        assertTokenType(TokenType.EOF, tokens.get(8).getType(), "EOF at end");
    }

    public void testMakeStatement() {
        Lexer lexer = new Lexer("make \"count 10");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals("make count 10 + EOF", 4, tokens.size());
        assertTokenType(TokenType.MAKE, tokens.get(0).getType(), "make keyword");
        assertTokenType(TokenType.STRING, tokens.get(1).getType(), "variable name as string");
        assertTokenValue("count", tokens.get(1).getValue(), "variable name");
        assertTokenType(TokenType.NUMBER, tokens.get(2).getType(), "number value");
        assertTokenValue("10", tokens.get(2).getValue(), "number 10");
    }

    public void testRepeatStatement() {
        Lexer lexer = new Lexer("repeat 4 [forward 100 right 90]");
        List<Token> tokens = lexer.tokenize();
        
        assertTokenType(TokenType.REPEAT, tokens.get(0).getType(), "repeat keyword");
        assertTokenType(TokenType.NUMBER, tokens.get(1).getType(), "repeat count");
        assertTokenValue("4", tokens.get(1).getValue(), "count 4");
        assertTokenType(TokenType.LBRACKET, tokens.get(2).getType(), "open bracket for block");
        assertTokenType(TokenType.FORWARD, tokens.get(3).getType(), "forward inside block");
        assertTokenType(TokenType.NUMBER, tokens.get(4).getType(), "distance");
        assertTokenType(TokenType.RIGHT, tokens.get(5).getType(), "right command");
        assertTokenType(TokenType.NUMBER, tokens.get(6).getType(), "angle");
        assertTokenType(TokenType.RBRACKET, tokens.get(7).getType(), "close bracket for block");
    }

    public void testCommentHandledToEndOfLine() {
        Lexer lexer = new Lexer(";this is a comment\nforward");
        List<Token> tokens = lexer.tokenize();

        assertTokenType(TokenType.COMMENT, tokens.get(0).getType(), "semicolon starts a comment");
        assertTokenValue("this is a comment", tokens.get(0).getValue(), "comment text should match");
        assertTokenType(TokenType.NEWLINE, tokens.get(1).getType(), "newline after comment");
        assertTokenType(TokenType.FORWARD, tokens.get(2).getType(), "next line still tokenized");
    }

    public void testComplexExpression() {
        Lexer lexer = new Lexer("sum 3.5 :x");
        List<Token> tokens = lexer.tokenize();
        
        assertEquals("sum decimal variable + EOF", 4, tokens.size());
        assertTokenType(TokenType.SUM, tokens.get(0).getType(), "sum keyword");
        assertTokenType(TokenType.NUMBER, tokens.get(1).getType(), "decimal number");
        assertTokenValue("3.5", tokens.get(1).getValue(), "decimal value");
        assertTokenType(TokenType.VARIABLE, tokens.get(2).getType(), "variable");
        assertTokenValue("x", tokens.get(2).getValue(), "variable name");
    }

}
