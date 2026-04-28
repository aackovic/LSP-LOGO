package com.lsplogo.server;

import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;



public class LogoLanguageServer implements LanguageServer, LanguageClientAware {

    private final LogoTextDocumentService textDocumentService;
    private final LogoWorkspaceService workspaceService;
    private boolean toShutdown;

    public LogoLanguageServer() {
        this.textDocumentService = new LogoTextDocumentService();
        this.workspaceService = new LogoWorkspaceService();
        this.toShutdown = false;
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);

        SemanticTokensLegend legend = new SemanticTokensLegend(
            Arrays.asList("keyword", "variable", "string", "number", "function", "operator", "comment"),
            Arrays.asList()
        );
        SemanticTokensWithRegistrationOptions semanticTokens = new SemanticTokensWithRegistrationOptions(legend);
        semanticTokens.setFull(true);
        capabilities.setSemanticTokensProvider(semanticTokens);
        capabilities.setDeclarationProvider(true);
        capabilities.setHoverProvider(true);

        InitializeResult result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        toShutdown = true;
        return CompletableFuture.completedFuture(new Object());
    }

    @Override
    public void exit() {
        System.exit(toShutdown ? 0 : 1);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return workspaceService;
    }

    @Override
    public void connect(LanguageClient client) {
        textDocumentService.connect(client);
        workspaceService.connect(client);
    }
}
