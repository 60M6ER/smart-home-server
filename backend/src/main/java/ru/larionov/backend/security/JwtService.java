package ru.larionov.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.larionov.backend.model.Role;
import ru.larionov.backend.model.User;
import ru.larionov.backend.services.UserService;

import javax.crypto.SecretKey;
import java.lang.reflect.Type;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    private static final long TIME_TO_LIVE_ACCESS_TOKEN = 1000L * 60 * 1;
    private static final long TIME_TO_LIVE_REFRESH_TOKEN = 1000L * 60 * 30;
    private SecretKey SECRET;

    @PostConstruct
    private void initJwtService() {
        SECRET = Jwts.SIG.HS256.key().build();
    }

    // Generate token with given user
    public String generateToken(UserDetails user, TypeToken typeToken) {
        switch (typeToken) {
            case ACCESS -> {return createToken(user);}
            case REFRESH -> {return createToken(user.getUsername());}
            default -> {return "";}
        }
    }

    // Create a JWT token with specified claims and subject (user)
    private String createToken(UserDetails userDetails) {
        Date now = new Date();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authority", userDetails.getAuthorities())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TIME_TO_LIVE_ACCESS_TOKEN))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    private String createToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + TIME_TO_LIVE_REFRESH_TOKEN))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Get the signing key for JWT token
    private SecretKey getSignKey() {
        return SECRET;
    }

    // Extract the username from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract a claim from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract all claims from the token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check if the token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate the token against user details and expiration
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
