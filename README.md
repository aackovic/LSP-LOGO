# lsp-logo

A Language Server Protocol (LSP) server for the LOGO programming language, written in Java.

## Features

This server currently supports:

- Syntax highlighting for LOGO keywords, numbers, strings, variables, comments, operators, and built-in functions
- Go-to-declaration for procedure and variable references
- Hover information for symbols

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
3. Set the command to the shaded jar (use absolute path):

Example of absolute path to jar file

```bash
java -jar /Users/andrej/IdeaProjects/lsp-logo/target/lsp-logo-1.0-SNAPSHOT.jar
```

4. Set the working directory to:

```bash
/Users/andrej/IdeaProjects/lsp-logo
```

5. Map the server to `*.logo` files.
6. Open a LOGO file. The server starts automatically.

## Example files

Sample LOGO files are included in the repository in the examples directory:

- `examples/test.logo`
- `examples/errors.logo`
- `examples/semantic-tokens.logo`

## Notes

The lexer treats `;`, `:` and `"` as the markers for comments, variables, and strings. The token range includes the marker character so highlighting covers the full LOGO form.
