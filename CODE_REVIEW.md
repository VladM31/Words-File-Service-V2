# Code Review

## Critical Issues

1. **Token key generation fails when the parent directory is missing**  
   When the token key file does not yet exist, `TokenGeneratorImpl` writes it directly with `Files.write(keyPath, ...)`. If the configured path contains a directory that hasn't been created, the write throws `NoSuchFileException`. Create the parent directories before writing the key file to make the bootstrap path resilient.【F:src/main/java/words/com/fileservicev2/domain/services/impls/TokenGeneratorImpl.java†L36-L47】


