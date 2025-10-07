package Him.admin.Services;

import Him.admin.Models.User;
import Him.admin.Models.Permission; // Import Permission
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set; // Import Set
import java.util.function.Function;
import java.util.stream.Collectors; // Import Collectors

@Service
public class JWTService {

    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.expiration-time}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration-time}")
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

    /**
     * Generates a JWT with rich claims, including userId, role, branch details, and permissions.
     */
    public String generateTokenForUser(User user) {
        Map<String, Object> claims = new HashMap<>();

        // Add core user details
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        // 1. Get and add Role
        String roleName = user.getRoles().stream()
                .findFirst() // Assuming a user has one primary role for this claim
                .map(role -> role.getName())
                .orElse("USER");
        claims.put("role", roleName);

        // 2. Get and add Branch details
        if (user.getBranch() != null) {
            // Adjust the getter methods (.getCode(), .getName()) to match your Branch entity
            claims.put("branchCode", user.getBranch().getBranchCode());
            claims.put("branch", user.getBranch().getBranchName());
        }

        // 3. Get and add Permissions
        Set<String> permissions = getPermissionsFromUser(user);
        claims.put("permissions", permissions);

        return createToken(claims, user.getUsername(), jwtExpiration);
    }

    /**
     * Extracts all unique permission strings ("module:action") from the user's roles.
     * NOTE: Relies on User.roles being EAGER, and for this to work,
     * the permissions linked to the roles must also be FETCHED either by EAGER or a custom query.
     */
    public Set<String> getPermissionsFromUser(User user) {
        if (user.getRoles() == null) {
            return Set.of();
        }

        return user.getRoles().stream()
                .flatMap(role -> {
                    // This stream relies on role.getPermissions() being initialized (not LAZY-loaded)
                    return role.getPermissions().stream();
                })
                // Use Permission.toString() which returns "module:action"
                .map(Permission::toString)
                .collect(Collectors.toSet());
    }

    // --- Existing methods kept for completeness ---

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