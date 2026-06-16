package com.dolsk.tyres.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility for token generation and validation.
 *
 * Algorithm: HS256 (HMAC-SHA256, requires ≥32 byte key)
 * Claims: sub (username), iat, exp
 * Expiry: configurable via jwt.expiration (default 7 days)
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Validates the JWT secret at startup — fails fast if missing or too short.
     * This prevents the app from starting without a proper secret.
     */
    @PostConstruct
    void validateSecret() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET environment variable is required but not set. "
                            + "Application cannot start without a valid JWT secret.");
        }
        if (jwtSecret.length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 characters for HS256. "
                            + "Current length: " + jwtSecret.length());
        }
        logger.info("JWT secret validated ({} chars)", jwtSecret.length());
    }

    // ── Token Generation ──────────────────────────────────────────────────────

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            logger.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    // ── Claim Extraction ──────────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Internal ──────────────────────────────────────────────────────────────

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
