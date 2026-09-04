package com.ticketrush.booking_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String baseString;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(baseString));
    }

    public String generateToken(Long userId) {
        return Jwts.builder().
                subject(String.valueOf(userId)).
                issuedAt(new Date()).
                expiration(new Date(System.currentTimeMillis() + expiration)).
                signWith(this.key).
                compact();
    }

    public Long getUserId(String token) {
        Claims claim =  Jwts.parser().
                verifyWith(this.key).
                build().
                parseSignedClaims(token).
                getPayload();

        return Long.parseLong(claim.getSubject());
    }
}
