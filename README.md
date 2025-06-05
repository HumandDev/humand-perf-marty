# Chats 2.0 Performance Testing

## Table of Contents

- [Setup](#setup)
- [Run Performance tests](#run-performance-tests)
- [Autoformatting of code files](#autoformatting-of-code-files)
- [Package name conventions](#package-name-conventions)
- [Nullability instrumentation](#nullability-instrumentation)

## Setup

1. Install sdkman
    1. `curl -s "https://get.sdkman.io" | bash`
    2. Replace .zshrc with .bashrc if using plain bash
   ```bash
       cat <<EOF >> .zshrc
       export SDKMAN_DIR="$HOME/.sdkman"
       [[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
       EOF
   ```
    3. Reload bash `source .zshrc`
2. Install java
    1. To view available versions use `sdk list java`
    2. For example install corretto version `sdk install java 21.0.5-amzn`
3. Install maven
    1. `sdk install maven`
4. Configure [humand artifact repository](https://www.notion.so/humand-co/AWS-CodeArtifact-1216757f313080f58b90fa005afca53f).
5. Install IntelliJ
   1. Install palantir-java-format plugin
6. Install project dependencies
   1. `mvn dependency:resolve`
7. Setup pre-commit script (if you want to check code format locally before any git commit)
   1. cp scripts/pre-commit .git/hooks/
   2. chmod +x .git/hooks/pre-commit
   3. Optional: If you want to apply spotless format before each commit uncomment the first part of the script and comment the second one

## Run Performance tests

```bash
./mvnw -pl marty-perf gatling:test
```

## Autoformatting of code files.

The project uses [Spotless](https://github.com/diffplug/spotless/tree/main/plugin-maven)
For java files it uses [Palantir](https://github.com/palantir/palantir-java-format) format rules,
that are an improvement over Google's java rules for a modern world aka after java 9+.
You can install IDE plugins or before pushing to git repository run:

```bash
./mvnw spotless:apply
```

# Package name conventions

Each maven module must use the following template as base java package:
`co.humand.comminications.marty.[module-name]`.

# Nullability instrumentation

We are using a java instrumentation and nullabilities annotations to assure that we do not have
`NullPointerExcetions`.
By default, it is assumed that everything parameter and return value is non-nullable.
