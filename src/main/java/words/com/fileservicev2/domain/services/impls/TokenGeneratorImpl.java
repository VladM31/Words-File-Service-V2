package words.com.fileservicev2.domain.services.impls;

import words.com.fileservicev2.domain.exceptions.ExpiredTokenException;
import words.com.fileservicev2.domain.exceptions.InvalidTokenException;
import words.com.fileservicev2.domain.services.TokenGenerator;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

class TokenGeneratorImpl implements TokenGenerator {
    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final int GCM_TAG_BITS = 128;     // 16 байт тега
    private static final int IV_LEN = 12;            // 12 байт nonce (рекомендовано для GCM)
    private static final byte VERSION = 0x01;        // версия формата токена

    private static final Base64.Encoder B64U_ENC = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder B64U_DEC = Base64.getUrlDecoder();
    private static final SecureRandom RNG = new SecureRandom();
    private final byte[] key;


    public TokenGeneratorImpl(Path keyPath) throws IOException, NoSuchAlgorithmException {
        if (Files.exists(keyPath)) {
            key = Files.readAllBytes(keyPath);
            return;
        }

        key = generateKey256();
        Files.write(keyPath, key,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.WRITE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
    }

    @Override
    public String create(Duration ttl, String plaintext) {
        if (ttl == null || ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("TTL must be positive");
        }
        long exp = Instant.now().plus(ttl).getEpochSecond();
        return createWithExpiry(key, exp, plaintext);
    }

    @Override
    public String parse(String token) {
        final byte[] raw;
        try {
            raw = B64U_DEC.decode(token);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("Malformed base64url");
        }

        if (raw.length < 1 + 8 + IV_LEN + 16) {
            throw new InvalidTokenException("Token too short");
        }

        ByteBuffer buf = ByteBuffer.wrap(raw);
        byte ver = buf.get();
        if (ver != VERSION) {
            throw new InvalidTokenException("Unsupported version: " + ver);
        }

        long exp = buf.getLong();
        long now = Instant.now().getEpochSecond();
        if (now > exp) {
            throw new ExpiredTokenException("Token expired");
        }

        byte[] iv = new byte[IV_LEN];
        buf.get(iv);
        byte[] ct = new byte[buf.remaining()];
        buf.get(ct);

        try {
            Cipher cipher = Cipher.getInstance(CIPHER);
            SecretKeySpec sk = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sk, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] pt = cipher.doFinal(ct); // бросит AEADBadTagException при подмене
            return new String(pt, StandardCharsets.UTF_8);
        } catch (AEADBadTagException badTag) {
            throw new InvalidTokenException("Invalid authentication tag (tampered or wrong key)");
        } catch (InvalidTokenException | ExpiredTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Token parse failed: " + e.getMessage());
        }
    }

    private byte[] generateKey256() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey k = kg.generateKey();
        return k.getEncoded();
    }

    private String createWithExpiry(byte[] key, long expiryEpochSeconds, String plaintext) {
        try {
            byte[] iv = new byte[IV_LEN];
            RNG.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER);
            SecretKeySpec sk = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, sk, new GCMParameterSpec(GCM_TAG_BITS, iv));

            byte[] pt = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ct = cipher.doFinal(pt);

            ByteBuffer buf = ByteBuffer.allocate(1 + 8 + IV_LEN + ct.length);
            buf.put(VERSION);
            buf.putLong(expiryEpochSeconds);
            buf.put(iv);
            buf.put(ct);

            return B64U_ENC.encodeToString(buf.array());
        } catch (Exception e) {
            throw new RuntimeException("Token create failed", e);
        }
    }

}
