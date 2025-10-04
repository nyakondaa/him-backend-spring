package Him.admin.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(unique = true, nullable = false)
    private String name;

    @Size(max = 200)
    private String description;

    // Permissions can be lazy
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Permission> permissions = new HashSet<>();

    // Avoid recursion: lazy + JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
}
