package Him.admin.Services;

import Him.admin.Models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${jwt.secret:mySecretKeymySecretKeymySecretKeymySecretKeymySecretKey}")
    private String secret;

    @Value("${jwt.expiration:900000}") // 15 minutes default
    private Long jwtExpiration;

    @Value("${jwt.refresh.expiration:604800000}") // 7 days default
    private Long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    // ✅ ADD THIS METHOD TO YOUR JWTService
    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Add all user details to the token
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        // ✅ FIX: Handle roles properly (assuming getRoles() returns a collection)
        String role = "USER";
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            // Get the first role, or extract role names as needed
            Object firstRole = user.getRoles().iterator().next();
            if (firstRole instanceof String) {
                role = (String) firstRole;
            } else {
                // If it's a Role entity, you might need to get the role name
                // role = firstRole.getName(); // Adjust based on your Role class
                role = firstRole.toString(); // Fallback
            }
        }
        claims.put("role", role);

        // ✅ FIX: Handle branch properly
        if (user.getBranch() != null) {
            claims.put("branchCode", user.getBranch().getBranchCode());
            // You might also want the branch name
            claims.put("branch", user.getBranch().getBranchName()); // Adjust field name as needed
        }

        // ✅ Consider adding permissions if available


        return createToken(claims, user.getUsername(), jwtExpiration);
    }

    public String generateTokenWithClaims(UserDetails userDetails, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>();
        if (additionalClaims != null) {
            claims.putAll(additionalClaims);
        }
        return createToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername(), refreshExpiration);
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getJwtExpiration() {
        return jwtExpiration;
    }
}