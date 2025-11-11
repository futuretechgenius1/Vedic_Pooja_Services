package com.vedicpooja.security;

import com.vedicpooja.auth.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key signingKey;
    private final String issuer;
    private final long ttlMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.issuer}") String issuer,
            @Value("${app.security.jwt.access-token-ttl-minutes}") long ttlMinutes
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(toBase64(secret));
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.issuer = issuer;
        this.ttlMinutes = ttlMinutes;
    }

    public String generateToken(Long userId, String emailOrPhone, Role role) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttlMinutes, ChronoUnit.MINUTES);
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(String.valueOf(userId))
                .addClaims(Map.of(
                        "uid", userId,
                        "subj", emailOrPhone == null ? "" : emailOrPhone,
                        "role", role.name()
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static String toBase64(String secret) {
        // If already base64, return as-is; else base64 encode
        try {
            Decoders.BASE64.decode(secret);
            return secret;
        } catch (IllegalArgumentException e) {
            return java.util.Base64.getEncoder().encodeToString(secret.getBytes());
        }
    }
}