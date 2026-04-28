package com.lsplogo.server;

import junit.framework.TestCase;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.TextDocumentItem;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.HoverParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Hover;
import org.eclipse.lsp4j.SemanticTokens;

public class ServerTest extends TestCase {

    public void testInitializeCapabilities() throws Exception {
        LogoLanguageServer server = new LogoLanguageServer();
        InitializeResult result = server.initialize(new InitializeParams()).get();
        ServerCapabilities caps = result.getCapabilities();

        assertTrue("text sync should be full", caps.getTextDocumentSync().toString().contains(TextDocumentSyncKind.Full.toString()));
        assertNotNull("semantic tokens should be provided", caps.getSemanticTokensProvider());
    }

    public void testHoverAndSemanticTokens() throws Exception {
        LogoTextDocumentService service = new LogoTextDocumentService();
        String uri = "file://test.logo";
        String code = "to square\nforward 100\nend\nmake \"count 10\nprint :count";

        TextDocumentItem item = new TextDocumentItem(uri, "logo", 1, code);
        DidOpenTextDocumentParams open = new DidOpenTextDocumentParams(item);
        service.didOpen(open);

        SemanticTokensParams stParams = new SemanticTokensParams(new TextDocumentIdentifier(uri));
        SemanticTokens tokens = service.semanticTokensFull(stParams).get();
        assertNotNull("semantic tokens response should not be null", tokens);
        assertTrue("semantic tokens should contain data", tokens.getData().size() > 0);

        HoverParams hParams = new HoverParams(new TextDocumentIdentifier(uri), new Position(4, 6));
        Hover hover = service.hover(hParams).get();
        assertNotNull("hover response should not be null", hover);
        assertTrue("hover content should reference variable name", hover.getContents().toString().contains("count"));
    }
}
