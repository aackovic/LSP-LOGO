package com.lsplogo.analysis;

import com.lsplogo.lexer.Lexer;
import com.lsplogo.lexer.Token;
import junit.framework.TestCase;
import java.util.List;

public class SymbolAnalyzerTest extends TestCase {

    public void testEmptyInput() {
        Lexer lexer = new Lexer("");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();
        assertEquals("empty input should have no symbols", 0, symbols.size());
    }

    public void testSimpleProcedureDeclaration() {
        Lexer lexer = new Lexer("to square\nforward 100\nend");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have one symbol", 1, symbols.size());
        Symbol sym = symbols.get(0);
        assertEquals("symbol name should match", "square", sym.name);
        assertEquals("symbol type should match", SymbolType.PROCEDURE, sym.type);
    }

    public void testProcedureWithParameters() {
        Lexer lexer = new Lexer("to rectangle :width :height\nforward :width\nright 90\nforward :height\nend");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have one symbol", 1, symbols.size());
        Symbol sym = symbols.get(0);
        assertEquals("procedure name should match", "rectangle", sym.name);
        assertEquals("procedure type should match", SymbolType.PROCEDURE, sym.type);
    }

    public void testNestedProcedures() {
        Lexer lexer = new Lexer("to outer\n  to inner\n  forward 50\n  end\nend");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have two symbols", 2, symbols.size());
        
        List<Symbol> procedures = analyzer.findSymbolsOfType(SymbolType.PROCEDURE);
        assertEquals("should have two procedures", 2, procedures.size());
    }

    public void testMakeVariableDeclaration() {
        Lexer lexer = new Lexer("make \"count 10");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have one symbol", 1, symbols.size());
        Symbol sym = symbols.get(0);
        assertEquals("variable name should match", "count", sym.name);
        assertEquals("variable type should match", SymbolType.VARIABLE, sym.type);
    }

    public void testLocalMakeVariableDeclaration() {
        Lexer lexer = new Lexer("localmake \"temp 42");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have one symbol", 1, symbols.size());
        Symbol sym = symbols.get(0);
        assertEquals("variable name should match", "temp", sym.name);
        assertEquals("variable type should match", SymbolType.VARIABLE, sym.type);
    }

    public void testMakeBeforeProcedure() {
        Lexer lexer = new Lexer("make \"x 5\nto func forward 50\nend");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have 2 symbols, make and to", 2, symbols.size());
        
        Symbol var = analyzer.findSymbol("x");
        assertNotNull("should find variable x", var);
        assertEquals("x should be variable", SymbolType.VARIABLE, var.type);
        Symbol toDecl = analyzer.findSymbol("func");
        assertNotNull("should find declaration func", toDecl);
        assertEquals("func should be procedure", SymbolType.PROCEDURE, toDecl.type);
    }


    public void testFindSymbolCaseInsensitive() {
        Lexer lexer = new Lexer("to square\nforward 50\nend");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        analyzer.analyze();

        Symbol found = analyzer.findSymbol("SQUARE");
        assertNotNull("should find symbol with different case", found);
        assertEquals("symbol name in lowercase", "square", found.name);
    }

    public void testLineColumnTracking() {
        Lexer lexer = new Lexer("to square\nforward 100\nend\nmake \"count 10");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        analyzer.analyze();

        Symbol procedureSymbol = analyzer.findSymbol("square");
        assertNotNull("should find square procedure", procedureSymbol);
        assertEquals("procedure declared at line 0", 0, procedureSymbol.line);

        Symbol variableSymbol = analyzer.findSymbol("count");
        assertNotNull("should find count variable", variableSymbol);
        assertEquals("variable declared at line 3", 3, variableSymbol.line);
    }

    public void testFindVariableWithColonPrefix() {
        Lexer lexer = new Lexer("make \"count 10\nprint :count");
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        analyzer.analyze();

        Symbol found = analyzer.findSymbol("count");
        assertNotNull("should resolve :count to declared variable", found);
        assertEquals("resolved symbol name", "count", found.name);
        assertEquals("resolved symbol type", SymbolType.VARIABLE, found.type);
    }

    public void testComplexProgram() {
        String code = "to square :size\n" +
                      "  repeat 4 [\n" +
                      "    forward :size\n" +
                      "    right 90\n" +
                      "  ]\n" +
                      "end\n" +
                      "\n" +
                      "make \"sideLength 100\n" +
                      "square :sideLength";

        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();
        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        List<Symbol> symbols = analyzer.analyze();

        assertEquals("should have two symbols", 2, symbols.size());

        Symbol square = analyzer.findSymbol("square");
        assertNotNull("should find square procedure", square);
        assertEquals("square is a procedure", SymbolType.PROCEDURE, square.type);

        Symbol sideLength = analyzer.findSymbol("sideLength");
        assertNotNull("should find sideLength variable", sideLength);
        assertEquals("sideLength is a variable", SymbolType.VARIABLE, sideLength.type);
    }

}
