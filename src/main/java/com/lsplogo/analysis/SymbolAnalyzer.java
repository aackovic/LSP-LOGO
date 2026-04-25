package com.lsplogo.analysis;

import com.lsplogo.lexer.Token;
import com.lsplogo.lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class SymbolAnalyzer {

    private List<Symbol> symbols;
    private List<Token> tokens;
    private int pos;

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public SymbolAnalyzer(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.symbols = new ArrayList<>();
    }
    
    /*
     analyzer scans through tokens and extracts:
     procedure declarations
     variable declarations
    */
    public List<Symbol> analyze() {
        symbols.clear();
        pos = 0;

        while (!endOfTokens()) {
            Token token = peek();

            if (token.getType() == TokenType.TO) {
                analyzeProcedureDeclaration();
            } 
            else if (token.getType() == TokenType.MAKE || token.getType() == TokenType.LOCALMAKE) {
                analyzeVariableDeclaration();
            } 
            else {
                advance();
            }
        }

        return symbols;
    }

    //to procname inputs ... statements ... end
    private void analyzeProcedureDeclaration() {
        advance();

        if (endOfTokens()) {
            return;
        }

        Token procToken = peek();
        if (procToken.getType() != TokenType.IDENTIFIER) {
            return;
        }

        String procName = procToken.getValue();
        int procLine = procToken.getLine();
        int procColumn = procToken.getColumn();

        advance();

        int depth = 1;
        while (!endOfTokens() && depth > 0) {
            Token t = peek();

            if (t.getType() == TokenType.TO) {
                analyzeProcedureDeclaration();
            } else if (t.getType() == TokenType.END) {
                depth--;
                advance();
            } else if (t.getType() == TokenType.MAKE || t.getType() == TokenType.LOCALMAKE) {
                analyzeVariableDeclaration();
            } else {
                advance();
            }
        }

        symbols.add(new Symbol(procName, SymbolType.PROCEDURE, procLine, procColumn));
    }

    // make varname expr || localmake varname expr
    private void analyzeVariableDeclaration() {
        advance();

        if (endOfTokens()) {
            return;
        }

        Token varToken = peek();
        if (varToken.getType() != TokenType.STRING && varToken.getType() != TokenType.VARIABLE) {
            return;
        }

        String varName = varToken.getValue();
        int varLine = varToken.getLine();
        int varColumn = varToken.getColumn();

        advance();

        symbols.add(new Symbol(varName, SymbolType.VARIABLE, varLine, varColumn));
    }

    public Symbol findSymbol(String name) {
        for (Symbol symbol : symbols) {
            if (symbol.name.equalsIgnoreCase(name)) {
                return symbol;
            }
        }
        return null;
    }

    public List<Symbol> findSymbolsOfType(SymbolType type) {
        List<Symbol> result = new ArrayList<>();
        for (Symbol symbol : symbols) {
            if (symbol.type == type) {
                result.add(symbol);
            }
        }
        return result;
    }

    private boolean endOfTokens() {
        return pos >= tokens.size();
    }

    private Token peek() {
        if (pos >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(pos);
    }

    private Token advance() {
        Token token = peek();
        if (!endOfTokens()) {
            pos++;
        }
        return token;
    }
}
