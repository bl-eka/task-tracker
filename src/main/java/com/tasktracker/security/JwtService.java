package com.tasktracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j; // ДОБАВЬТЕ ЭТОТ ИМПОРТ
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j // ДОБАВЬТЕ ЭТУ АННОТАЦИЮ
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        String role = extractClaim(token, claims -> claims.get("role", String.class));
        log.info("JWT role extracted: {}", role);  // ✅ Используем логгер
        return role;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        // БЕЗОПАСНОЕ ПОЛУЧЕНИЕ РОЛИ!
        String role = "ROLE_USER"; // Значение по умолчанию

        if (userDetails != null && userDetails.getAuthorities() != null) {
            var authorities = userDetails.getAuthorities();
            if (!authorities.isEmpty()) {
                role = authorities.iterator().next().getAuthority();
            }
        }

        extraClaims.put("role", role);

        log.info("Generating token for: {}, role: {}", userDetails.getUsername(), role);

        return Jwts.builder()
                .claims(extraClaims)                      // Новый метод: .claims() вместо .setClaims()
                .subject(userDetails.getUsername())       // Новый метод: .subject() вместо .setSubject()
                .issuedAt(new Date(System.currentTimeMillis())) // Новый метод: .issuedAt() вместо .setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + expiration)) // Новый метод: .expiration() вместо .setExpiration()
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Указываем алгоритм явно для ясности
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())                // Новый метод: .verifyWith() вместо .setSigningKey()
                .build()
                .parseSignedClaims(token)                  // Новый метод: .parseSignedClaims() вместо .parseClaimsJws()
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Для обратной совместимости с JwtAuthenticationFilter
    public boolean validateToken(String token) {
        try {
            log.debug("Validating JWT token...");
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token);
            log.debug("JWT token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token format: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
}