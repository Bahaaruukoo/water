package com.UserManagement.account.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

public class JwtTokenDecoder {

   // private String secret = "your-secret-key"; // Make sure it's Base64 encoded if using URL-safe Base64
   @Value("${jwt.secret}")
   private String secret;

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
