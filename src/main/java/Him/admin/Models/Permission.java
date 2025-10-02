package Him.admin.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Module name is required")
    @Size(max = 50)
    private String module; // e.g., "users", "transactions", "budgets"

    @NotBlank(message = "Action is required")
    @Size(max = 50)
    private String action; // e.g., "read", "create", "update", "delete"

    // Many-to-many relationship back to roles
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        Permission that = (Permission) o;
        return module.equalsIgnoreCase(that.module) && action.equalsIgnoreCase(that.action);
    }

    @Override
    public int hashCode() {
        return (module.toLowerCase() + action.toLowerCase()).hashCode();
    }

    @Override
    public String toString() {
        return module + ":" + action;
    }


}
