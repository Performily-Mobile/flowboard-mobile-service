package com.performily.flowboard.iam.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    @Value("${iam.jwt.secret:}")
    private String secret;

    @Value("${iam.jwt.ttl-seconds:28800}")
    private long ttl;

    private final ObjectMapper mapper = new ObjectMapper();

    private byte[] sign(String input) {
        try {
            if (secret.length() < 32)
                throw new IllegalStateException("Configure IAM_JWT_SECRET with at least 32 characters");
            Mac m = Mac.getInstance("HmacSHA256");
            m.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return m.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String b64(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    public String issue(Long id, long credentialVersion) {
        long now = Instant.now().getEpochSecond();
        String header = b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = b64("{\"sub\":\"" + id + "\",\"jti\":\"" + UUID.randomUUID() + "\",\"ver\":" + credentialVersion + ",\"iat\":" + now + ",\"exp\":" + (now + ttl) + "}");
        String unsigned = header + "." + payload;
        return unsigned + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(unsigned));
    }

    public Claims verify(String token) {
        try {
            String[] p = token.split("\\.");
            if (p.length != 3)
                throw new IllegalArgumentException();
            
            byte[] expected = sign(p[0] + "." + p[1]);
            if (!java.security.MessageDigest.isEqual(expected, Base64.getUrlDecoder().decode(p[2])))
                throw new IllegalArgumentException();
            
            String header = new String(Base64.getUrlDecoder().decode(p[0]), StandardCharsets.UTF_8);
            if (!"HS256".equals(mapper.readTree(header).path("alg").asText()))
                throw new IllegalArgumentException();

            String json = new String(Base64.getUrlDecoder().decode(p[1]), StandardCharsets.UTF_8);
            var claims = mapper.readTree(json);
            String sub = claims.path("sub").asText();
            String jti = claims.path("jti").asText();
            long exp = claims.path("exp").asLong(0);
            long version = claims.path("ver").asLong(-1);
            if (jti.isBlank() || sub.isBlank() || version < 0)
                throw new IllegalArgumentException();
            
            if (exp <= Instant.now().getEpochSecond())
                throw new IllegalArgumentException();
            
            return new Claims(Long.valueOf(sub), jti, Instant.ofEpochSecond(exp), version);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
    }

    public record Claims(Long accountId, String tokenId, Instant expiresAt, long credentialVersion) {}
}