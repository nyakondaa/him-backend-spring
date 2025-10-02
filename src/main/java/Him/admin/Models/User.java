
package Him.admin.Models;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // encrypted (BCrypt)

    private String firstName;
    private String lastName;

    private Date birthDate;
    private String address;

    private boolean active = true; // active/inactive status

    private boolean locked = false; // locked after failed login attempts

    private int failedLoginAttempts = 0; // track login failures

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch; // optional branch assignment

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles; // supports multiple roles per user

    // Convenience method to check if user has a specific permission
    public boolean hasPermission(String module, String action) {
        if (roles == null) return false;
        return roles.stream()
                .anyMatch(role -> role.getPermissions().stream()
                        .anyMatch(p -> p.getModule().equalsIgnoreCase(module)
                                && p.getAction().equalsIgnoreCase(action)));
    }
}
