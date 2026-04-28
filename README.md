# lsp-logo

A Language Server Protocol (LSP) server for the LOGO programming language, written in Java 17.

## Features

This server currently supports:

- Syntax highlighting for LOGO keywords, numbers, strings, variables, comments, operators, and built-in functions
- Go-to-declaration for procedure and variable references
- Hover information for symbols

**Project layout**

Working directory example:

- `/Users/andrej/IdeaProjects/lsp-logo/`

Inside of this directory which is essentially a maven project is all the code.
The structure of the project is following:

- `src/main/java/com/lsplogo/lexer`: `Lexer`, `Token` and `TokenType` classes that tokenize LOGO source.
- `src/main/java/com/lsplogo/analysis`: `Symbol`, `SymbolAnalyzer` and `SymbolType` analyze for symbols for procedure and variable declarations.
- `src/main/java/com/lsplogo/server`: LSP server implementation (`LogoLanguageServer`, `LogoTextDocumentService`, `LogoWorkspaceService`) and helper classes.
- `src/test/java`: Unit tests (`LexerTest`, `SymbolAnalyzerTest`, `ServerTest`).
- `examples/`: Example `.logo` files used for manual testing and demonstration.
- `pom.xml`: Maven build and packaging (compiler and Shade plugin to create the runnable jar).
- `src/main/java/com/lsplogo/App.java`: Application entry point (main class) used by the packaged jar.


## Testing

Use Maven to run tests for the project:

```bash
mvn test
```

## Build

Use Maven to build the project and produce the runnable jar:

```bash
mvn clean package
```

This creates the shaded jar at:

```bash
target/lsp-logo-1.0-SNAPSHOT.jar
```

## IntelliJ IDEA

This project is intended to be used through a language server client.

1. Open the project in IntelliJ IDEA.
2. Let Maven import finish.
3. Build the project with `mvn clean package`.
4. Open a LOGO file such as `examples/test.logo`
5. Configure your LSP client or plugin to start the server automatically.

### lsp4ij setup steps

1. Install the `lsp4ij` plugin in IntelliJ IDEA.
2. Open the language server configuration for this project.
3. Set the command to the shaded jar (use absolute path).

Example of absolute path to jar file:

```bash
java -jar /Users/andrej/IdeaProjects/lsp-logo/target/lsp-logo-1.0-SNAPSHOT.jar
```

<img width="1219" height="717" alt="Image" src="https://github.com/user-attachments/assets/75da96c8-7f8b-44db-b3ee-5eed8660efc8" />

4. Map the server to `*.logo` files.

<img width="1222" height="720" alt="Image" src="https://github.com/user-attachments/assets/27e4faad-b908-4760-b658-f99aaa284500" />
   
5. Open a LOGO file. The server starts automatically.
If the server doesn't start automatically, restart it manually in language server tab

## Example files

Sample LOGO files are included in the repository in the examples directory:

- `examples/test.logo`
- `examples/errors.logo`
- `examples/semantic-tokens.logo`

## Notes

The lexer treats `;`, `:` and `"` as the markers for comments, variables, and strings. The token range includes the marker character so highlighting covers the full LOGO form.
