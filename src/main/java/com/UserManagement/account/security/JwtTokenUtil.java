package com.UserManagement.account.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
public class JwtTokenUtil {

    //private final String secret = "your-secret-key";  // Keep this secret
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long tokenLife;

    @Value(("${jwt.refreshTokenExpirationTime}"))
    private long refreshTokenLife;

    // Retrieve username from the token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Retrieve roles from the token
    public List<String> getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.get("roles", List.class);
    }

    // Generate token for user
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        claims.put("roles", roles);
        return doGenerateToken(claims, userDetails.getUsername());
    }
    // Generate token for user
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        claims.put("roles", roles);
        return doGenerateRefreshToken(claims, userDetails.getUsername());
    }

    /*
    // Generate token
public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    List<String> roles = userDetails.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    claims.put("roles", roles);

    return doGenerateToken(claims, userDetails.getUsername());
}

  */
    // Validate and decode token
    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Generate token with claims and subject
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + tokenLife)) //1000 * 60 * 60 * 10))  // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    // Generate token with claims and subject
    private String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTokenLife)) //1000 * 60 * 60 * 10))  // Token valid for 10 hours
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    // Validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Retrieve claims from token
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public void validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)  // Use the same secret key
                    .parseClaimsJws(token)     // Parse the token, this will throw an exception if tampered
                    .getBody();

            System.out.println("Valid token. Subject: " + claims.getSubject());
        } catch (SignatureException e) {
            // Token has been tampered with
            System.out.println("Invalid JWT signature. The token has been tampered with.");
            throw new RuntimeException("Invalid JWT signature. The token has been tampered with.");
        } catch (ExpiredJwtException e) {
            // Token has expired
            System.out.println("Expired JWT token.");
            throw new RuntimeException("Token has Expired");

        } catch (JwtException e) {
            // Invalid token
            System.out.println("Invalid JWT token.");
            throw new RuntimeException("Invalid JWT token.");

        }
    }
    // Add method to validate refresh token, if necessary
    public boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


}
