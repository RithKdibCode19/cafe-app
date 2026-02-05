package com.example.backend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class QrCodeService {

    // In a real app, this should be in application.properties and rotating
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 30000; // 30 seconds

    public String generateQrToken(Long branchId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("branchId", branchId);
        claims.put("nonce", UUID.randomUUID().toString()); // Prevent replay attacks

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("branchId", Long.class);
        } catch (Exception e) {
            // Token expired or invalid
            return null;
        }
    }
}
