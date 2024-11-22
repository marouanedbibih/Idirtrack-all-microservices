package com.example.api_gateway.jwt;
import java.util.HashMap;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
        private static final String SECRET_KEY = "QAR1At+Vv3rNXrmiHGcZ0tG3+EPgdkeVi/HqjgcUbf8WcgdgpCXDTjb7CnIv9WEL";

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    // Extract role from token
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    // Extract session from token
    public String extractSession(String token) {
        return extractClaim(token, claims -> claims.get("session", String.class));
    }


    // Validate token
    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername())) && !isTokenExpired(token);

    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date from token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract claim from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims cliams = extractAllClaims(token);
        return claimsResolver.apply(cliams);
    }

    // Extract all claims from token
    public Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Get signing key
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
