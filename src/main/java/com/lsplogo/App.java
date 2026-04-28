package com.lsplogo;

import com.lsplogo.server.LogoLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

public class App 
{
    public static void main( String[] args )  throws Exception {
        LogoLanguageServer server = new LogoLanguageServer();

        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);
        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);

        launcher.startListening().get();
    }
}
