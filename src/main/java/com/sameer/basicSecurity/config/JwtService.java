package com.sameer.basicSecurity.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    // Secret Key for signing the JWT. It should be kept private.
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    // Extracts the userName from the JWT token.
    //return -> The userName contained in the token.
   public String extractUsername(String token) {
       // Extract and return the subject claim from the token

       return extractClaim(token,Claims::getSubject);
    }
// Extracts a specific claim from the JWT token.
    // claimResolver A function to extract the claim.
    // return-> The value of the specified claim.
    public <T> T extractClaim(String token, Function<Claims,T>claimsResolver) {
        final  Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generates a JWT token for the given userName.
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    //refreshToken


    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        // Build JWT token with claims, subject, issued time, expiration time, and signing algorithm
        // Token valid for 3 minutes
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*3))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token,UserDetails userDetails) {
        // Extract username from token and check if it matches UserDetails' username
        final String username = extractUsername(token);
        // Also check if the token is expired
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //Checks if the JWT token is expired.
    //return-> True if the token is expired, false otherwise.
    private boolean isTokenExpired(String token) {
        // Check if the token's expiration time is before the current time
        return extractExpiration(token).before(new Date());
    }

    // Extracts the expiration date from the JWT token.
    //@return The expiration date of the token.
    private Date extractExpiration(String token) {
        // Extract and return the expiration claim from the token
        return extractClaim(token,Claims::getExpiration);
    }
//Extracts all claims from the JWT token.
    //return-> Claims object containing all claims.
    private Claims extractAllClaims(String token) {
        try {
            // Parse and return all claims from the token
            return Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println("Error parsing JWT: " + e.getMessage());
            throw e;
        }
    }
    // Creates a signing key from the base64 encoded secret.
    //returns a Key object for signing the JWT.
    private Key getSignInKey() {
        byte[] keysBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keysBytes);
    }
}
