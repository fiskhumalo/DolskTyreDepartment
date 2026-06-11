//package com.dolsk.tyres.security;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.JwtException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Component
//public class JwtUtil {
//
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
//
//    @Value("${jwt.secret}")
//    private String jwtSecret;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpirationMs;
//
//    public String generateToken(UserDetails userDetails) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//    /**
//     * Generates a signed JWT.
//     * Uses the non-deprecated signWith(Key) API from JJWT 0.11+.
//     */
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return Jwts.builder()
//                .setClaims(extraClaims)
//                .setSubject(userDetails.getUsername())
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(getSigningKey())
//                .compact();
//    }
//
//    /**
//     * Returns true only when the token is structurally valid, correctly signed,
//     * not expired, and the subject matches the provided UserDetails.
//     */
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        try {
//            final String username = extractUsername(token);
//            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//        } catch (JwtException | IllegalArgumentException ex) {
//            logger.warn("Token validation failed: {}", ex.getMessage());
//            return false;
//        }
//    }
//
//    /**
//     * Extracts the username (subject) from the token.
//     * Throws JwtException (runtime) if the token is malformed, expired, or tampered.
//     * Callers (JwtAuthFilter) must catch this.
//     */
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractClaim(token, Claims::getExpiration).before(new Date());
//    }
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        return claimsResolver.apply(extractAllClaims(token));
//    }
//
//    /**
//     * Parses and verifies the JWT signature.
//     * Throws JwtException subclasses on any parse/validation failure.
//     */
//    private Claims extractAllClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(getSigningKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    private Key getSigningKey() {
//        byte[] keyBytes = jwtSecret.getBytes();
//        // HMAC-SHA256 requires minimum 32 bytes; 256-bit key.
//        // The application.properties default is 44 chars, which satisfies this.
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}
package com.dolsk.tyres.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // ================= TOKEN GENERATION =================

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // ================= VALIDATION =================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException ex) {
            logger.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    // ================= CLAIM EXTRACTION =================

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

    // ================= CORE PARSER =================

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException ex) {
            logger.warn("Invalid JWT: {}", ex.getMessage());
            throw ex;
        }
    }

    // ================= SIGNING KEY =================

    private Key getSigningKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters long");
        }

        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}