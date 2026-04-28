package com.lsplogo.server;

import com.lsplogo.lexer.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;


public class PositionUtil {

    public static Range tokenToRange(Token token) {
        Position start = new Position(token.getLine(), token.getColumn());
        Position end = new Position(token.getLine(), token.getColumn() + token.getLength());
        return new Range(start, end);
    }

    public static Position fromToken(Token token) {
        return new Position(token.getLine(), token.getColumn());
    }

    public static boolean isPositionInRange(Position pos, Range range) {
        int posLine = pos.getLine();
        int posChar = pos.getCharacter();
        int startLine = range.getStart().getLine();
        int startChar = range.getStart().getCharacter();
        int endLine = range.getEnd().getLine();
        int endChar = range.getEnd().getCharacter();

        if (posLine < startLine || posLine > endLine) {
            return false;
        }
        if (posLine == startLine && posChar < startChar) {
            return false;
        }
        if (posLine == endLine && posChar >= endChar) {
            return false;
        }
        return true;
    }
}
