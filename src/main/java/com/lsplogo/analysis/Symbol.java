package com.lsplogo.analysis;

public class Symbol {
    public final String name;
    public final SymbolType type;
    public final int line;
    public final int column;

    public Symbol(String name, SymbolType type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
    }

    @Override
    public String toString() {
        return type + " " + name + " at line " + line + " column " + column;
    }
}
