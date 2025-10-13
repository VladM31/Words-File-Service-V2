# Code Review

## Critical Issues

1. **DomainServiceConfig is not a Spring configuration**  
   `DomainServiceConfig` declares a collection of `@Bean` methods but the class itself is not annotated with `@Configuration` (or otherwise registered). Without that annotation the class will not be picked up by component scanning, so none of the domain service beans (upload services, download service, token generator, etc.) will be created and every injection point that relies on them will fail at runtime. Please add `@Configuration` (or equivalent explicit registration) to restore the bean definitions.【F:src/main/java/words/com/fileservicev2/domain/services/impls/DomainServiceConfig.java†L19-L159】

2. **Temporary file creation in OujingzhouImageContentAnalyzer is broken on Unix-like systems**  
   `File.createTempFile` forbids path separators in the `prefix` argument. On non-Windows OSes `AppUtils.getFilePrefixByOs()` returns `"/"`, so the call `File.createTempFile(AppUtils.getFilePrefixByOs() + "temp_image_", ...)` throws `IllegalArgumentException`, preventing NSFW validation from ever running. Drop the prefix hack and pass just a simple prefix such as `"temp_image_"`; the target directory is already supplied via the third argument.【F:src/main/java/words/com/fileservicev2/domain/services/impls/OujingzhouImageContentAnalyzer.java†L24-L36】

3. **ExecutorService leak in FileDeleteServiceImpl**  
   `FileDeleteServiceImpl` creates a fixed thread pool but its `close()` implementation is empty. Because the service is `AutoCloseable`, callers will expect it to release resources; leaving the pool running causes a thread leak (and prevents a graceful shutdown in tests). Ensure `close()` shuts the pool down (e.g., `executorService.shutdown()` with await termination).【F:src/main/java/words/com/fileservicev2/domain/services/impls/FileDeleteServiceImpl.java†L19-L58】

4. **Token key generation fails when the parent directory is missing**  
   When the token key file does not yet exist, `TokenGeneratorImpl` writes it directly with `Files.write(keyPath, ...)`. If the configured path contains a directory that hasn't been created, the write throws `NoSuchFileException`. Create the parent directories before writing the key file to make the bootstrap path resilient.【F:src/main/java/words/com/fileservicev2/domain/services/impls/TokenGeneratorImpl.java†L36-L47】

## Additional Observations

- `ImageUploadService.canUpload` relies on a case-sensitive `String.endsWith` check without ensuring the file name actually ends with `"." + extension`. A file named `picturejpeg` (no dot) or one with uppercase extensions will bypass/skip the filter. Consider normalising and matching against `"." + ext` for correctness.【F:src/main/java/words/com/fileservicev2/domain/services/impls/ImageUploadService.java†L37-L45】

- The comment `// Запрещённый контент` next to `return false` in `OujingzhouImageContentAnalyzer` contradicts the return value (false means “allowed”). This is minor, but it can easily confuse future readers.【F:src/main/java/words/com/fileservicev2/domain/services/impls/OujingzhouImageContentAnalyzer.java†L44-L60】

