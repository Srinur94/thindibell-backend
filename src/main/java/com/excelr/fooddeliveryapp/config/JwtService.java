package com.excelr.fooddeliveryapp.config;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.excelr.fooddeliveryapp.entity.User;
import com.excelr.fooddeliveryapp.exception.CustomJwtException;
import com.excelr.fooddeliveryapp.repository.UserRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserRepository userRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Extract username (usually email) from JWT
    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        } catch (Exception e) {
            throw new CustomJwtException("Failed to extract username from token", HttpStatus.UNAUTHORIZED);
        }
    }

    // Generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate JWT token with user roles
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        return buildToken(claims, userDetails);
    }

    // Build JWT token with claims
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())  // email or username
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token with user details
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Check if JWT expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Core JWT parsing logic
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException("JWT token expired", HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            throw new CustomJwtException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            throw new CustomJwtException("Token parsing error", HttpStatus.UNAUTHORIZED);
        }
    }

    // Return JWT signing key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Get logged-in user from SecurityContext
    public User getCurrentUser(HttpServletRequest httpRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomJwtException("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomJwtException("User not found: " + username, HttpStatus.NOT_FOUND));
    }
}
