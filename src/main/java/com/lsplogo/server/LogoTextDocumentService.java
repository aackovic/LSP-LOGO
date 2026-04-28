package com.lsplogo.server;

import com.lsplogo.analysis.Symbol;
import com.lsplogo.analysis.SymbolAnalyzer;
import com.lsplogo.lexer.Lexer;
import com.lsplogo.lexer.Token;
import org.eclipse.lsp4j.DeclarationParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.MarkupContent;
import org.eclipse.lsp4j.MarkupKind;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.TextDocumentContentChangeEvent;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class LogoTextDocumentService implements TextDocumentService {

    private final Map<String, String> documents = new HashMap<>();

    public void connect(LanguageClient client) {

    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        String text = params.getTextDocument().getText();
        documents.put(uri, text);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        List<TextDocumentContentChangeEvent> changes = params.getContentChanges();
        if (changes == null || changes.isEmpty()) {
            return;
        }
        String fullText = changes.get(changes.size() - 1).getText();
        documents.put(uri, fullText);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        String uri = params.getTextDocument().getUri();
        documents.remove(uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {

    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documents.get(uri);
        if (text == null) {
            return CompletableFuture.completedFuture(new SemanticTokens(new ArrayList<>()));
        }

        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        List<Integer> data = new ArrayList<>();
        int lastLine = 0;
        int lastColumn = 0;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            int line = token.getLine();
            int column = token.getColumn();
            int length = token.getLength();

            if (!SemanticTokenMapper.shouldIncludeToken(token)) {
                continue;
            }

            String tokenType = resolveSemanticTokenType(tokens, i);
            if (tokenType == null) {
                continue;
            }

            int diffLine = line - lastLine;
            int diffColumn = (diffLine == 0) ? (column - lastColumn) : column;
            int tokenTypeIndex = getTokenTypeIndex(tokenType);

            data.add(diffLine);
            data.add(diffColumn);
            data.add(length);
            data.add(tokenTypeIndex);
            data.add(0);

            lastLine = line;
            lastColumn = column;
        }

        return CompletableFuture.completedFuture(new SemanticTokens(data));
    }

    private String resolveSemanticTokenType(List<Token> tokens, int index) {
        Token token = tokens.get(index);
        return SemanticTokenMapper.mapTokenType(token.getType());
    }

    private int getTokenTypeIndex(String type) {
        switch (type) {
            case SemanticTokenMapper.KEYWORD:
                return 0;
            case SemanticTokenMapper.VARIABLE:
                return 1;
            case SemanticTokenMapper.STRING:
                return 2;
            case SemanticTokenMapper.NUMBER:
                return 3;
            case SemanticTokenMapper.FUNCTION:
                return 4;
            case SemanticTokenMapper.OPERATOR:
                return 5;
            case SemanticTokenMapper.COMMENT:
                return 6;
            default:
                return 7;
        }
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends org.eclipse.lsp4j.LocationLink>>> declaration(DeclarationParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documents.get(uri);
        if (text == null) {
            return CompletableFuture.completedFuture(null);
        }

        Position currPos = params.getPosition();

        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        Token symbolToken = findTokenAtPosition(tokens, currPos);
        if (symbolToken == null) {
            return CompletableFuture.completedFuture(null);
        }

        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        analyzer.analyze();

        Symbol symbol = analyzer.findSymbol(symbolToken.getValue());
        if (symbol == null) {
            return CompletableFuture.completedFuture(null);
        }

        Position startPos = new Position(symbol.line, symbol.column);
        Position endPos = new Position(symbol.line, symbol.column + symbol.name.length());
        Range range = new Range(startPos, endPos);
        Location location = new Location(uri, range);
        
        List<Location> locations = new ArrayList<>();
        locations.add(location);
        return CompletableFuture.completedFuture(Either.forLeft(locations));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documents.get(uri);
        if (text == null) {
            return CompletableFuture.completedFuture(null);
        }

        Position cursorPosition = params.getPosition();

        Lexer lexer = new Lexer(text);
        List<Token> tokens = lexer.tokenize();

        Token symbolToken = findTokenAtPosition(tokens, cursorPosition);
        if (symbolToken == null) {
            return CompletableFuture.completedFuture(null);
        }

        SymbolAnalyzer analyzer = new SymbolAnalyzer(tokens);
        analyzer.analyze();

        Symbol symbol = analyzer.findSymbol(symbolToken.getValue());
        if (symbol == null) {
            return CompletableFuture.completedFuture(null);
        }

        String content = String.format(
            "**%s** `%s`\n\nDeclared at line %d",
            symbol.type.toString(),
            symbol.name,
            symbol.line + 1
        );

        MarkupContent markup = new MarkupContent(MarkupKind.MARKDOWN, content);
        Hover hover = new Hover(markup);
        return CompletableFuture.completedFuture(hover);
    }

    private Token findTokenAtPosition(List<Token> tokens, Position position) {
        for (Token token : tokens) {
            Range range = PositionUtil.tokenToRange(token);
            if (PositionUtil.isPositionInRange(position, range)) {
                return token;
            }
        }
        return null;
    }
}
