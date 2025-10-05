package Him.admin.Controllers;
import Him.admin.DTO.RolesDTO.ResponseRole;
import Him.admin.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<Set<ResponseRole>> getAllRoles() {
        Set<ResponseRole> roles = roleRepository.findAll()
                .stream()
                .map(role -> new ResponseRole(
                        role.getName(),
                        role.getDescription()

                ))
                .collect(Collectors.toSet());
        return ResponseEntity.ok(roles);
    }
}
